package org.jgll_staged.lookup

import java.util.ArrayDeque
import java.util.Collections
import java.util.Deque
import java.util.HashSet
import java.util.Set
import org.jgll_staged.grammar.Grammar
import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.parser.Descriptor
import org.jgll_staged.parser.GSSEdge
import org.jgll_staged.parser.GSSNode
import org.jgll_staged.sppf.DummyNode
import org.jgll_staged.sppf.NonPackedNode
import org.jgll_staged.sppf.NonterminalSymbolNode
import org.jgll_staged.sppf.PackedNode
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.sppf.TerminalSymbolNode
import org.jgll_staged.util.Input
import org.jgll_staged.util.hashing.CuckooHashMap
import org.jgll_staged.util.hashing.CuckooHashSet
import org.jgll_staged.util.logging.LoggerWrapper
import RecursiveDescentLookupTable._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object RecursiveDescentLookupTable {

  private val log = LoggerWrapper.getLogger(classOf[RecursiveDescentLookupTable])
}

class RecursiveDescentLookupTable(grammar: Grammar) extends AbstractLookupTable(grammar) {

  private var descriptorsStack: Deque[Descriptor] = new ArrayDeque()

  private var descriptorsSet: CuckooHashSet[Descriptor] = new CuckooHashSet(Descriptor.externalHasher)

  private var terminals: Array[TerminalSymbolNode] = _

  private var nonPackedNodes: CuckooHashSet[NonPackedNode] = new CuckooHashSet(NonPackedNode.externalHasher)

  private val gssNodes = new CuckooHashSet(GSSNode.externalHasher)

  private val packedNodes = new CuckooHashSet(PackedNode.externalHasher)

  private val gssEdges = new CuckooHashSet(GSSEdge.externalHasher)

  @BeanProperty
  var nonPackedNodesCount: Int = _

  private var poppedElements: CuckooHashMap[GSSNode, Set[SPPFNode]] = new CuckooHashMap(GSSNode.externalHasher)

  override def init(input: Input) {
    terminals = Array.ofDim[TerminalSymbolNode](2 * input.size)
    descriptorsStack.clear()
    descriptorsSet.clear()
    nonPackedNodes.clear()
    gssNodes.clear()
    packedNodes.clear()
    gssEdges.clear()
    poppedElements.clear()
    nonPackedNodesCount = 0
  }

  override def getGSSNode(grammarSlot: GrammarSlot, inputIndex: Int): GSSNode = {
    val key = new GSSNode(grammarSlot, inputIndex)
    val value = gssNodes.add(key)
    if (value == null) {
      return key
    }
    value
  }

  override def getGSSNodesCount(): Int = gssNodes.size

  override def getGSSNodes(): java.lang.Iterable[GSSNode] = gssNodes

  override def hasNextDescriptor(): Boolean = !descriptorsStack.isEmpty

  override def nextDescriptor(): Descriptor = descriptorsStack.pop()

  override def addDescriptor(descriptor: Descriptor): Boolean = {
    if (descriptorsSet.contains(descriptor)) {
      return false
    }
    descriptorsStack.push(descriptor)
    descriptorsSet.add(descriptor)
    true
  }

  override def getTerminalNode(terminalIndex: Int, leftExtent: Int): TerminalSymbolNode = {
    var index = 2 * leftExtent
    if (terminalIndex != TerminalSymbolNode.EPSILON) {
      index = index + 1
    }
    var terminal = terminals(index)
    if (terminal == null) {
      terminal = new TerminalSymbolNode(terminalIndex, leftExtent)
      log.trace("Terminal node created: %s", terminal)
      terminals(index) = terminal
      nonPackedNodesCount += 1
    }
    terminal
  }

  override def getNonPackedNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Int): NonPackedNode = {
    val key = createNonPackedNode(slot, leftExtent, rightExtent)
    var value = nonPackedNodes.add(key)
    if (value == null) {
      value = key
    }
    value
  }

  override def getStartSymbol(startSymbol: HeadGrammarSlot, inputSize: Int): NonterminalSymbolNode = {
    nonPackedNodes.get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1)).asInstanceOf[NonterminalSymbolNode]
  }

  override def getDescriptorsCount(): Int = descriptorsSet.size

  override def addPackedNode(parent: NonPackedNode, 
      slot: GrammarSlot, 
      pivot: Int, 
      leftChild: SPPFNode, 
      rightChild: SPPFNode) {
    if (parent.getCountPackedNode == 0) {
      if (leftChild != DummyNode.getInstance) {
        parent.addChild(leftChild)
      }
      parent.addChild(rightChild)
      parent.addFirstPackedNode(slot, pivot)
    } else if (parent.getCountPackedNode == 1) {
      if (parent.getFirstPackedNodeGrammarSlot == slot && parent.getFirstPackedNodePivot == pivot) {
        return
      } else {
        val packedNode = new PackedNode(slot, pivot, parent)
        val firstPackedNode = parent.addSecondPackedNode(packedNode, leftChild, rightChild)
        packedNodes.add(packedNode)
        packedNodes.add(firstPackedNode)
      }
    } else {
      val key = new PackedNode(slot, pivot, parent)
      if (packedNodes.add(key) == null) {
        parent.addPackedNode(key, leftChild, rightChild)
      }
    }
  }

  override def getPackedNodesCount(): Int = packedNodes.size

  override def hasGSSEdge(source: GSSNode, label: SPPFNode, destination: GSSNode): Boolean = {
    val edge = new GSSEdge(source, label, destination)
    val added = gssEdges.add(edge) == null
    if (added) {
      source.addGSSEdge(edge)
    }
    !added
  }

  override def getGSSEdgesCount(): Int = gssEdges.size

  override def addToPoppedElements(gssNode: GSSNode, sppfNode: SPPFNode) {
    var set = poppedElements.get(gssNode)
    if (set == null) {
      set = new HashSet()
      poppedElements.put(gssNode, set)
    }
    log.trace("Added to P: %s -> %s", gssNode, sppfNode)
    set.add(sppfNode)
  }

  override def getSPPFNodesOfPoppedElements(gssNode: GSSNode): java.lang.Iterable[SPPFNode] = {
    val set = poppedElements.get(gssNode)
    if (set == null) {
      return Collections.emptySet()
    }
    set
  }
}
