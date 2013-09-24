package org.jgll.recognizer

import java.util.ArrayDeque
import java.util.Deque
import org.jgll.grammar.Grammar
import org.jgll.grammar.slot.BodyGrammarSlot
import org.jgll.grammar.slot.GrammarSlot
import org.jgll.grammar.slot.HeadGrammarSlot
import org.jgll.grammar.slot.L0
import org.jgll.grammar.slot.StartSlot
import org.jgll.util.hashing.CuckooHashSet
import org.jgll.util.logging.LoggerWrapper
import org.jgll.util.InputTrait

import AbstractGLLRecognizer._
object AbstractGLLRecognizer {

  private val log = LoggerWrapper.getLogger(classOf[AbstractGLLRecognizer])

  val startSlot = new StartSlot("Start")

  val u0 = GSSNode.U0
}

abstract class AbstractGLLRecognizer extends GLLRecognizer {

  protected var input: Input = _

  protected var ci: Int = _

  protected var cu: GSSNode = u0

  protected var grammar: Grammar = _

  protected var startSymbol: HeadGrammarSlot = _

  protected var descriptorSet: CuckooHashSet[Descriptor] = _

  protected var descriptorStack: Deque[Descriptor] = _

  protected var gssNodes: CuckooHashSet[GSSNode] = _

  protected var recognized: Boolean = _

  protected var endIndex: Rep[Int] = _

  override def recognize(input: Input, grammar: Grammar, nonterminalName: String): Boolean = {
    val startSymbol = grammar.getNonterminalByName(nonterminalName)
    if (startSymbol == null) {
      throw new RuntimeException("No nonterminal named " + nonterminalName + " found")
    }
    init(grammar, input, 0, input.size - 1, startSymbol)
    cu = create(startSlot, cu, ci)
    val start = System.nanoTime()
    L0.getInstance.recognize(this, input, startSymbol)
    val end = System.nanoTime()
    logStatistics(end - start)
    recognized
  }

  override def recognize(input: Input,
      startIndex: Int,
      endIndex: Rep[Int],
      fromSlot: BodyGrammarSlot): Boolean = {
    init(grammar, input, startIndex, endIndex, null)
    cu = create(startSlot, cu, ci)
    val start = System.nanoTime()
    add(fromSlot, cu, ci)
    L0.getInstance.recognize(this, input)
    val end = System.nanoTime()
    logStatistics(end - start)
    recognized
  }

  protected def logStatistics(duration: Long) {
    log.debug("Recognition Time: %d ms", duration / 1000000)
    val mb = 1024 * 1024
    val runtime = Runtime.getRuntime
    log.debug("Memory used: %d mb", (runtime.totalMemory() - runtime.freeMemory()) / mb)
  }

  protected def init(grammar: Grammar,
      input: Input,
      startIndex: Int,
      endIndex: Rep[Int],
      startSymbol: HeadGrammarSlot) {
    this.grammar = grammar
    this.startSymbol = startSymbol
    this.input = input
    this.ci = startIndex
    this.endIndex = endIndex
    this.recognized = false
    this.cu = u0
    if (descriptorSet == null) {
      descriptorSet = new CuckooHashSet(Descriptor.externalHasher)
    } else {
      descriptorSet.clear()
    }
    if (descriptorStack == null) {
      descriptorStack = new ArrayDeque()
    } else {
      descriptorStack.clear()
    }
    if (gssNodes == null) {
      gssNodes = new CuckooHashSet(GSSNode.externalHasher)
    } else {
      gssNodes.clear()
    }
  }

  override def add(slot: GrammarSlot, u: GSSNode, inputIndex: Int) {
    val descriptor = new Descriptor(slot, u, inputIndex)
    if (descriptorSet.add(descriptor) == null) {
      log.trace("Descriptor added: %s : true", descriptor)
      descriptorStack.push(descriptor)
    } else {
      log.trace("Descriptor %s : false", descriptor)
    }
  }

  override def pop(u: GSSNode, i: Int) {
    log.trace("Pop %s, %d", u.getGrammarSlot, i)
    if (u != u0) {
      u.addPoppedIndex(i)
      for (node <- u.getChildren) {
        add(u.getGrammarSlot, node, i)
      }
    }
  }

  override def create(L: GrammarSlot, u: GSSNode, i: Int): GSSNode = {
    log.trace("GSSNode created: (%s, %d)", L, i)
    val key = new GSSNode(L, i)
    var v = gssNodes.add(key)
    if (v == null) {
      v = key
    }
    if (!v.hasChild(u)) {
      v.addChild(u)
      for (index <- v.getPoppedIndices) {
        add(L, u, index)
      }
    }
    v
  }

  override def hasNextDescriptor(): Boolean = !descriptorStack.isEmpty

  override def nextDescriptor(): Descriptor = descriptorStack.pop()

  override def update(gssNode: GSSNode, inputIndex: Int) {
    this.ci = inputIndex
    this.cu = gssNode
  }

  override def recognitionError(gssNode: GSSNode, inputIndex: Int) {
  }

  override def getCi(): Int = ci

  override def getCu(): GSSNode = cu
}
