package org.jgll.lookup

import org.jgll.grammar.slot.{GrammarSlotTrait,HeadGrammarSlotTrait}
import org.jgll.parser.{GSSNodeTrait, DescriptorTrait}
import org.jgll.sppf._
import org.jgll.util.InputTrait
import collection.mutable.Set

trait LookupTableTrait {
  self: InputTrait
   with DescriptorTrait
   with TerminalSymbolNodeTrait
   with NonPackedNodeTrait
   with HeadGrammarSlotTrait
   with NonterminalSymbolNodeTrait
   with GrammarSlotTrait
   with SPPFNodeTrait
   with GSSNodeTrait =>
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

    def getStartSymbol(startSymbol: HeadGrammarSlot, inputSize: Rep[Int]): NonterminalSymbolNode

    def hasGSSEdge(source: GSSNode, label: SPPFNode, destination: GSSNode): Boolean

    def getGSSNode(label: GrammarSlot, inputIndex: Int): GSSNode

    def addToPoppedElements(gssNode: GSSNode, sppfNode: SPPFNode): Unit

    def getSPPFNodesOfPoppedElements(gssNode: GSSNode): Set[SPPFNode]

    def getNonPackedNodesCount(): Int

    def getPackedNodesCount(): Int

    def getGSSNodesCount(): Int

    def getGSSEdgesCount(): Int

    def getDescriptorsCount(): Int

    def getGSSNodes(): Iterable[GSSNode]

    def init(input: Input): Unit
  }
}