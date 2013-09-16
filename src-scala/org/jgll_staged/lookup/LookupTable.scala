package org.jgll_staged.lookup

import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.parser.Descriptor
import org.jgll_staged.parser.GSSNode
import org.jgll_staged.sppf.NonPackedNode
import org.jgll_staged.sppf.NonterminalSymbolNode
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.sppf.TerminalSymbolNode
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

trait LookupTable {

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

  def getStartSymbol(startSymbol: HeadGrammarSlot, inputSize: Int): NonterminalSymbolNode

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
