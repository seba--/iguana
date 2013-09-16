package org.jgll_staged.parser

import org.jgll_staged.grammar.Grammar
import org.jgll_staged.grammar.Keyword
import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.grammar.slot.KeywordGrammarSlot
import org.jgll_staged.grammar.slot.L0
import org.jgll_staged.grammar.slot.LastGrammarSlot
import org.jgll_staged.grammar.slot.NonterminalGrammarSlot
import org.jgll_staged.grammar.slot.TerminalGrammarSlot
import org.jgll_staged.grammar.slotaction.SlotAction
import org.jgll_staged.lookup.LookupTable
import org.jgll_staged.sppf.DummyNode
import org.jgll_staged.sppf.NonPackedNode
import org.jgll_staged.sppf.NonterminalSymbolNode
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.sppf.TerminalSymbolNode
import org.jgll_staged.util.Input
import org.jgll_staged.util.logging.LoggerWrapper
import GLLParserImpl._
//remove if not needed
import scala.collection.JavaConversions._

object GLLParserImpl {

  private val log = LoggerWrapper.getLogger(classOf[GLLParserImpl])

  protected val u0 = GSSNode.U0
}

class GLLParserImpl(protected var lookupTable: LookupTable) extends GLLParser with GLLParserInternals {

  import GLLParserImpl._

  protected var cu: GSSNode = u0

  protected var cn: SPPFNode = DummyNode.getInstance

  protected var ci: Int = 0

  protected var input: Input = _

  protected var grammar: Grammar = _

  protected var errorSlot: GrammarSlot = _

  protected var errorIndex: Int = -1

  protected var errorGSSNode: GSSNode = _

  override def parse(input: Input, grammar: Grammar, startSymbolName: String): NonterminalSymbolNode = {
    val startSymbol = grammar.getNonterminalByName(startSymbolName)
    if (startSymbol == null) {
      throw new RuntimeException("No nonterminal named " + startSymbolName + " found")
    }
    this.input = input
    this.grammar = grammar
    init()
    lookupTable.init(input)
    val start = System.nanoTime()
    L0.getInstance.parse(this, input, startSymbol)
    val end = System.nanoTime()
    val root = lookupTable.getStartSymbol(startSymbol, input.size)
    if (root == null) {
      throw new ParseError(errorSlot, this.input, errorIndex, errorGSSNode)
    }
    logParseStatistics(end - start)
    root
  }

  private def logParseStatistics(duration: Long) {
    log.info("Parsing Time: " + duration / 1000000 + " ms")
    val mb = 1024 * 1024
    val runtime = Runtime.getRuntime
    log.info("Memory used: %d mb", (runtime.totalMemory() - runtime.freeMemory()) / mb)
    log.debug("Descriptors: %d", lookupTable.getDescriptorsCount)
    log.debug("Non-packed nodes: %d", lookupTable.getNonPackedNodesCount)
    log.debug("Packed nodes: %d", lookupTable.getPackedNodesCount)
    log.debug("GSS Nodes: %d", lookupTable.getGSSNodesCount)
    log.debug("GSS Edges: %d", lookupTable.getGSSEdgesCount)
  }

  override def recordParseError(slot: GrammarSlot) {
    if (errorIndex >= this.errorIndex) {
      log.trace("Error recorded at %s %d", this, ci)
      this.errorIndex = ci
      this.errorSlot = slot
      this.errorGSSNode = cu
    }
  }

  private def init() {
    cu = u0
    cn = DummyNode.getInstance
    ci = 0
    errorSlot = null
    errorIndex = -1
    errorGSSNode = null
  }

  private def add(label: GrammarSlot, 
      u: GSSNode, 
      inputIndex: Int, 
      w: SPPFNode) {
    val descriptor = new Descriptor(label, u, inputIndex, w)
    val result = lookupTable.addDescriptor(descriptor)
    log.trace("Descriptor created: %s : %b", descriptor, result)
  }

  override def addDescriptor(label: GrammarSlot) {
    add(label, cu, ci, DummyNode.getInstance)
  }

  override def pop() {
    if (cu != u0) {
      log.trace("Pop %s, %d, %s", cu.getGrammarSlot, ci, cn)
      for (popAction <- cu.getGrammarSlot.asInstanceOf[BodyGrammarSlot].getPopActions if popAction.execute(this, 
        input)) {
        return
      }
      lookupTable.addToPoppedElements(cu, cn)
      for (edge <- cu.getEdges) {
        assert(cu.getGrammarSlot.isInstanceOf[BodyGrammarSlot])
        val slot = cu.getGrammarSlot
        var y: SPPFNode = null
        y = if (slot.isInstanceOf[LastGrammarSlot]) getNonterminalNode(slot.asInstanceOf[LastGrammarSlot], 
          edge.getSppfNode, cn) else getIntermediateNode(slot.asInstanceOf[BodyGrammarSlot], edge.getSppfNode, 
          cn)
        add(cu.getGrammarSlot, edge.getDestination, ci, y)
      }
    }
  }

  override def createGSSNode(slot: GrammarSlot) {
    cu = create(slot, cu, ci, cn)
  }

  private def create(L: GrammarSlot, 
      u: GSSNode, 
      i: Int, 
      w: SPPFNode): GSSNode = {
    log.trace("GSSNode created: (%s, %d)", L, i)
    val v = lookupTable.getGSSNode(L, i)
    if (!lookupTable.hasGSSEdge(v, w, u)) {
      for (z <- lookupTable.getSPPFNodesOfPoppedElements(v)) {
        var x: SPPFNode = null
        x = if (L.isInstanceOf[LastGrammarSlot]) getNonterminalNode(L.asInstanceOf[LastGrammarSlot], 
          w, z) else getIntermediateNode(L.asInstanceOf[BodyGrammarSlot], w, z)
        add(L, u, z.getRightExtent, x)
      }
    }
    v
  }

  override def getTerminalNode(c: Int): TerminalSymbolNode = lookupTable.getTerminalNode(c, ci += 1)

  override def getEpsilonNode(): TerminalSymbolNode = {
    lookupTable.getTerminalNode(TerminalSymbolNode.EPSILON, ci)
  }

  override def getNonterminalNode(slot: LastGrammarSlot, rightChild: SPPFNode): SPPFNode = {
    cn = getNonterminalNode(slot, cn, rightChild)
    cn
  }

  override def getIntermediateNode(slot: BodyGrammarSlot, rightChild: SPPFNode): SPPFNode = {
    cn = getIntermediateNode(slot, cn, rightChild)
    cn
  }

  def getNonterminalNode(slot: LastGrammarSlot, leftChild: SPPFNode, rightChild: SPPFNode): SPPFNode = {
    val t = slot.getHead
    var leftExtent: Int = 0
    val rightExtent = rightChild.getRightExtent
    leftExtent = if (leftChild != DummyNode.getInstance) leftChild.getLeftExtent else rightChild.getLeftExtent
    val newNode = lookupTable.getNonPackedNode(t, leftExtent, rightExtent).asInstanceOf[NonPackedNode]
    lookupTable.addPackedNode(newNode, slot, rightChild.getLeftExtent, leftChild, rightChild)
    newNode
  }

  def getIntermediateNode(slot: BodyGrammarSlot, leftChild: SPPFNode, rightChild: SPPFNode): SPPFNode = {
    val previous = slot.previous()
    if (previous.isFirst) {
      if (previous.isInstanceOf[TerminalGrammarSlot] || previous.isInstanceOf[KeywordGrammarSlot]) {
        return rightChild
      } else if (previous.isInstanceOf[NonterminalGrammarSlot] && !previous.isNullable) {
        return rightChild
      }
    }
    var leftExtent: Int = 0
    val rightExtent = rightChild.getRightExtent
    leftExtent = if (leftChild != DummyNode.getInstance) leftChild.getLeftExtent else rightChild.getLeftExtent
    val newNode = lookupTable.getNonPackedNode(slot, leftExtent, rightExtent).asInstanceOf[NonPackedNode]
    lookupTable.addPackedNode(newNode, slot, rightChild.getLeftExtent, leftChild, rightChild)
    newNode
  }

  override def hasNextDescriptor(): Boolean = lookupTable.hasNextDescriptor()

  override def nextDescriptor(): Descriptor = {
    val descriptor = lookupTable.nextDescriptor()
    ci = descriptor.getInputIndex
    cu = descriptor.getGSSNode
    cn = descriptor.getSPPFNode
    log.trace("Processing (%s, %s, %s, %s)", descriptor.getGrammarSlot, ci, cu, cn)
    descriptor
  }

  override def getCurrentInputIndex(): Int = ci

  override def getCurrentGSSNode(): GSSNode = cu

  override def getKeywordStub(keyword: Keyword, slot: HeadGrammarSlot, inputIndex: Int): NonPackedNode = {
    val nextIndex = inputIndex + keyword.size
    val node = lookupTable.getNonPackedNode(slot, inputIndex, nextIndex)
    node.addFirstPackedNode(slot.getAlternateAt(0).getLastSlot.next(), nextIndex)
    node.asInstanceOf[NonterminalSymbolNode].setKeywordNode(true)
    ci = nextIndex
    node
  }

  override def getLookupTable(): LookupTable = lookupTable
}
