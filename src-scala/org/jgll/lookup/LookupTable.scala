package org.jgll.lookup

import org.jgll.grammar.slot.GrammarSlot
import org.jgll.grammar.slot.HeadGrammarSlot
import org.jgll.parser.Descriptor
import org.jgll.parser.GSSNode
import org.jgll.sppf.NonPackedNode
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.util.{InputTrait, Input}
//remove if not needed
import scala.collection.JavaConversions._

trait LookupTable extends InputTrait {

  def hasNextDescriptor(): Boolean

  def nextDescriptor(): Descriptor

  def addDescriptor(descriptor: Descriptor): Boolean

  def getTerminalNode(terminalIndex: Int, leftExtent: Int): TerminalSymbolNode

  def getNonPackedNode(grammarSlot: GrammarSlot, leftExtent: Int, rightExtent: Int): NonPackedNode

  def addPackedNode(parent: NonPackedNode, 
      slot: GrammarSlot, 
      pivot: Int, 
      leftChild: SPPFNode, 
      rightChild: SPPFNode): Unit

  def getStartSymbol(startSymbol: HeadGrammarSlot, inputSize: Rep[Int]): NonterminalSymbolNode

  def hasGSSEdge(source: GSSNode, label: SPPFNode, destination: GSSNode): Boolean

  def getGSSNode(label: GrammarSlot, inputIndex: Int): GSSNode

  def addToPoppedElements(gssNode: GSSNode, sppfNode: SPPFNode): Unit

  def getSPPFNodesOfPoppedElements(gssNode: GSSNode): java.lang.Iterable[SPPFNode]

  def getNonPackedNodesCount(): Int

  def getPackedNodesCount(): Int

  def getGSSNodesCount(): Int

  def getGSSEdgesCount(): Int

  def getDescriptorsCount(): Int

  def getGSSNodes(): java.lang.Iterable[GSSNode]

  def init(input: Input): Unit
}
