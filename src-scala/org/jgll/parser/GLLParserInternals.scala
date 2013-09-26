package org.jgll.parser

import org.jgll.lookup.LookupTableTrait
import org.jgll.sppf._
import org.jgll.grammar.slot.{HeadGrammarSlotTrait, LastGrammarSlotTrait, BodyGrammarSlotTrait, GrammarSlotTrait}
import org.jgll.grammar.KeywordTrait

trait GLLParserInternalsTrait {
  self: BodyGrammarSlotTrait
   with KeywordTrait
   with SPPFNodeTrait
   with LastGrammarSlotTrait
   with DescriptorTrait
   with HeadGrammarSlotTrait
   with TerminalSymbolNodeTrait
   with NonPackedNodeTrait
   with LookupTableTrait
   with GrammarSlotTrait
   with GSSNodeTrait =>

  trait GLLParserInternals {

    def pop(): Unit

    def createGSSNode(slot: GrammarSlot): Unit

    def getTerminalNode(c: Int): TerminalSymbolNode

    def getEpsilonNode(): TerminalSymbolNode

    def getNonterminalNode(slot: LastGrammarSlot, rightChild: SPPFNode): SPPFNode

    def getIntermediateNode(slot: BodyGrammarSlot, rightChild: SPPFNode): SPPFNode

    def addDescriptor(label: GrammarSlot): Unit

    def getKeywordStub(keyword: Keyword, slot: HeadGrammarSlot, ci: Int): NonPackedNode

    def hasNextDescriptor(): Boolean

    def nextDescriptor(): Descriptor

    def getCurrentInputIndex(): Int

    def getCurrentGSSNode(): GSSNode

    def recordParseError(slot: GrammarSlot): Unit

    def getLookupTable(): LookupTable
  }
}