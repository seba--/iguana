package org.jgll_staged.lookup

import java.util.ArrayDeque
import java.util.Collections
import java.util.HashSet
import java.util.Queue
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
import LevelBasedLookupTable._
//remove if not needed
import scala.collection.JavaConversions._

object LevelBasedLookupTable {

  private val log = LoggerWrapper.getLogger(classOf[LevelBasedLookupTable])
}

class LevelBasedLookupTable(grammar: Grammar, private var chainLength: Int) extends AbstractLookupTable(grammar) {

  private var currentLevel: Int = _

  private var countNonPackedNodes: Int = _

  private var terminals: Array[Array[TerminalSymbolNode]] = new Array[Array[TerminalSymbolNode]](chainLength + 1, 2)

  private var u: CuckooHashSet[Descriptor] = new CuckooHashSet(getSize, Descriptor.levelBasedExternalHasher)

  private var forwardDescriptors: Array[CuckooHashSet[Descriptor]] = new Array[CuckooHashSet[Descriptor]](chainLength)

  private var currentNodes: CuckooHashSet[NonPackedNode] = new CuckooHashSet(initialSize, NonPackedNode.levelBasedExternalHasher)

  private var forwardNodes: Array[CuckooHashSet[NonPackedNode]] = new Array[CuckooHashSet[NonPackedNode]](chainLength)

  private var currentPackedNodes: CuckooHashSet[PackedNode] = new CuckooHashSet(initialSize, PackedNode.levelBasedExternalHasher)

  private var forwardPackedNodes: Array[CuckooHashSet[PackedNode]] = new Array[CuckooHashSet[PackedNode]](chainLength)

  private var r: Queue[Descriptor] = new ArrayDeque()

  private var forwardRs: Array[Queue[Descriptor]] = new Array[Queue[Descriptor]](chainLength)

  private var currentGssNodes: CuckooHashSet[GSSNode] = new CuckooHashSet(initialSize, GSSNode.levelBasedExternalHasher)

  private var forwardGssNodes: Array[CuckooHashSet[GSSNode]] = new Array[CuckooHashSet[GSSNode]](chainLength)

  private var currendEdges: CuckooHashSet[GSSEdge] = new CuckooHashSet(initialSize, GSSEdge.levelBasedExternalHasher)

  private var forwardEdges: Array[CuckooHashSet[GSSEdge]] = new Array[CuckooHashSet[GSSEdge]](chainLength)

  private var currentPoppedElements: CuckooHashMap[GSSNode, Set[SPPFNode]] = new CuckooHashMap(initialSize, 
    GSSNode.levelBasedExternalHasher)

  private var forwardPoppedElements: Array[CuckooHashMap[GSSNode, Set[SPPFNode]]] = new Array[CuckooHashMap[GSSNode, Set[SPPFNode]]](chainLength)

  private var countGSSNodes: Int = _

  private var size: Int = _

  private var all: Int = _

  private var countPackedNodes: Int = _

  protected var countGSSEdges: Int = _

  private val initialSize = 2048

  for (i <- 0 until chainLength) {
    forwardDescriptors(i) = new CuckooHashSet(getSize, Descriptor.levelBasedExternalHasher)
    forwardRs(i) = new ArrayDeque(initialSize)
    forwardNodes(i) = new CuckooHashSet(initialSize, NonPackedNode.levelBasedExternalHasher)
    forwardGssNodes(i) = new CuckooHashSet(initialSize, GSSNode.levelBasedExternalHasher)
    forwardEdges(i) = new CuckooHashSet(initialSize, GSSEdge.levelBasedExternalHasher)
    forwardPoppedElements(i) = new CuckooHashMap(initialSize, GSSNode.levelBasedExternalHasher)
    forwardPackedNodes(i) = new CuckooHashSet(PackedNode.levelBasedExternalHasher)
  }

  def this(grammar: Grammar) {
    this(grammar, grammar.getLongestTerminalChain)
  }

  private def gotoNextLevel() {
    val nextIndex = indexFor(currentLevel + 1)
    val tmpDesc = u
    u.clear()
    u = forwardDescriptors(nextIndex)
    forwardDescriptors(nextIndex) = tmpDesc
    val tmpR = r
    assert(r.isEmpty)
    r = forwardRs(nextIndex)
    forwardRs(nextIndex) = tmpR
    val tmpNonPackedNode = currentNodes
    currentNodes.clear()
    currentNodes = forwardNodes(nextIndex)
    forwardNodes(nextIndex) = tmpNonPackedNode
    val tmpPackedNode = currentPackedNodes
    currentPackedNodes.clear()
    currentPackedNodes = forwardPackedNodes(nextIndex)
    forwardPackedNodes(nextIndex) = tmpPackedNode
    val tmpGSSNodeSet = currentGssNodes
    currentGssNodes.clear()
    currentGssNodes = forwardGssNodes(nextIndex)
    forwardGssNodes(nextIndex) = tmpGSSNodeSet
    val tmpGSSEdgeSet = currendEdges
    currendEdges.clear()
    currendEdges = forwardEdges(nextIndex)
    forwardEdges(nextIndex) = tmpGSSEdgeSet
    val tmpPoppedElements = currentPoppedElements
    currentPoppedElements.clear()
    currentPoppedElements = forwardPoppedElements(nextIndex)
    forwardPoppedElements(nextIndex) = tmpPoppedElements
    terminals(indexFor(currentLevel))(0) = null
    terminals(indexFor(currentLevel))(1) = null
    currentLevel += 1
  }

  private def indexFor(inputIndex: Int): Int = inputIndex % chainLength

  override def getNonPackedNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Int): NonPackedNode = {
    var newNodeCreated = false
    val key = createNonPackedNode(slot, leftExtent, rightExtent)
    var value: NonPackedNode = null
    if (rightExtent == currentLevel) {
      value = currentNodes.add(key)
      if (value == null) {
        countNonPackedNodes += 1
        newNodeCreated = true
        value = key
      }
    } else {
      val index = indexFor(rightExtent)
      value = forwardNodes(index).add(key)
      if (value == null) {
        countNonPackedNodes += 1
        newNodeCreated = true
        value = key
      }
    }
    log.trace("SPPF node created: %s : %b", value, newNodeCreated)
    value
  }

  override def getTerminalNode(terminalIndex: Int, leftExtent: Int): TerminalSymbolNode = {
    var newNodeCreated = false
    var index2: Int = 0
    var rightExtent: Int = 0
    if (terminalIndex == -2) {
      rightExtent = leftExtent
      index2 = 1
    } else {
      rightExtent = leftExtent + 1
      index2 = 0
    }
    val index = indexFor(rightExtent)
    var terminal = terminals(index)(index2)
    if (terminal == null) {
      terminal = new TerminalSymbolNode(terminalIndex, leftExtent)
      countNonPackedNodes += 1
      terminals(index)(index2) = terminal
      newNodeCreated = true
    }
    log.trace("SPPF Terminal node created: %s : %b", terminal, newNodeCreated)
    terminal
  }

  override def getStartSymbol(startSymbol: HeadGrammarSlot, inputSize: Int): NonterminalSymbolNode = {
    var currentNodes = this.currentNodes
    if (currentLevel != inputSize - 1) {
      val index = indexFor(inputSize - 1)
      currentNodes = forwardNodes(index)
    }
    currentNodes.get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1)).asInstanceOf[NonterminalSymbolNode]
  }

  override def getNonPackedNodesCount(): Int = countNonPackedNodes

  override def hasNextDescriptor(): Boolean = size > 0

  override def nextDescriptor(): Descriptor = {
    if (!r.isEmpty) {
      size -= 1
      r.remove()
    } else {
      gotoNextLevel()
      nextDescriptor()
    }
  }

  private def getSize(): Int = grammar.getMaxDescriptorsAtInput

  override def addDescriptor(descriptor: Descriptor): Boolean = {
    val inputIndex = descriptor.getInputIndex
    if (inputIndex == currentLevel) {
      if (u.add(descriptor) == null) {
        r.add(descriptor)
        size += 1
        all += 1
      } else {
        return false
      }
    } else {
      val index = indexFor(descriptor.getInputIndex)
      if (forwardDescriptors(index).add(descriptor) == null) {
        forwardRs(index).add(descriptor)
        size += 1
        all += 1
      } else {
        return false
      }
    }
    true
  }

  override def getDescriptorsCount(): Int = all

  override def getGSSNode(label: GrammarSlot, inputIndex: Int): GSSNode = {
    val key = new GSSNode(label, inputIndex)
    var value: GSSNode = null
    if (inputIndex == currentLevel) {
      value = currentGssNodes.add(key)
      if (value == null) {
        countGSSNodes += 1
        value = key
      }
    } else {
      val index = indexFor(inputIndex)
      value = forwardGssNodes(index).add(key)
      if (value == null) {
        countGSSNodes += 1
        value = key
      }
    }
    value
  }

  override def hasGSSEdge(source: GSSNode, label: SPPFNode, destination: GSSNode): Boolean = {
    val edge = new GSSEdge(source, label, destination)
    if (source.getInputIndex == currentLevel) {
      val added = currendEdges.add(edge) == null
      if (added) {
        countGSSEdges += 1
        source.addGSSEdge(edge)
      }
      !added
    } else {
      val index = indexFor(source.getInputIndex)
      val added = forwardEdges(index).add(edge) == null
      if (added) {
        countGSSEdges += 1
        source.addGSSEdge(edge)
      }
      !added
    }
  }

  override def getGSSNodesCount(): Int = countGSSNodes

  override def getGSSNodes(): java.lang.Iterable[GSSNode] = {
    throw new UnsupportedOperationException()
  }

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
        if (parent.getRightExtent == currentLevel) {
          currentPackedNodes.add(packedNode)
          currentPackedNodes.add(firstPackedNode)
        } else {
          val index = indexFor(parent.getRightExtent)
          forwardPackedNodes(index).add(packedNode)
          forwardPackedNodes(index).add(firstPackedNode)
        }
        log.trace("Packed node created : %s", firstPackedNode)
        log.trace("Packed node created : %s", packedNode)
        countPackedNodes += 2
      }
    } else {
      val key = new PackedNode(slot, pivot, parent)
      if (parent.getRightExtent == currentLevel) {
        if (currentPackedNodes.add(key) == null) {
          parent.addPackedNode(key, leftChild, rightChild)
          countPackedNodes += 1
        }
      } else {
        val index = indexFor(parent.getRightExtent)
        if (forwardPackedNodes(index).add(key) == null) {
          parent.addPackedNode(key, leftChild, rightChild)
          countPackedNodes += 1
        }
      }
      log.trace("Packed node created : %s", key)
    }
  }

  override def getPackedNodesCount(): Int = countPackedNodes

  override def getGSSEdgesCount(): Int = countGSSEdges

  override def addToPoppedElements(gssNode: GSSNode, sppfNode: SPPFNode) {
    if (gssNode.getInputIndex == currentLevel) {
      var set = currentPoppedElements.get(gssNode)
      if (set == null) {
        set = new HashSet()
        currentPoppedElements.put(gssNode, set)
      }
      set.add(sppfNode)
    } else {
      val index = indexFor(gssNode.getInputIndex)
      var set = forwardPoppedElements(index).get(gssNode)
      if (set == null) {
        set = new HashSet()
      }
      set.add(sppfNode)
    }
  }

  override def getSPPFNodesOfPoppedElements(gssNode: GSSNode): java.lang.Iterable[SPPFNode] = {
    if (gssNode.getInputIndex == currentLevel) {
      var set = currentPoppedElements.get(gssNode)
      if (set == null) {
        set = Collections.emptySet()
      }
      set
    } else {
      val index = indexFor(gssNode.getInputIndex)
      var set = forwardPoppedElements(index).get(gssNode)
      if (set == null) {
        set = Collections.emptySet()
      }
      set
    }
  }

  override def init(input: Input) {
    terminals = Array.ofDim[TerminalSymbolNode](chainLength + 1, 2)
    u.clear()
    r.clear()
    currentNodes.clear()
    currentPackedNodes.clear()
    currentGssNodes.clear()
    currendEdges.clear()
    currentPoppedElements.clear()
    for (i <- 0 until chainLength) {
      forwardDescriptors(i).clear()
      forwardRs(i).clear()
      forwardNodes(i).clear()
      forwardGssNodes(i).clear()
      forwardEdges(i).clear()
      forwardPoppedElements(i).clear()
      forwardPackedNodes(i).clear()
    }
    currentLevel = 0
    countNonPackedNodes = 0
    countGSSNodes = 0
    size = 0
    all = 0
    countPackedNodes = 0
    countGSSEdges = 0
  }
}