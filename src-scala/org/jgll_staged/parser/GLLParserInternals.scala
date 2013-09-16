package org.jgll_staged.parser

import org.jgll_staged.grammar.Keyword
import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.grammar.slot.LastGrammarSlot
import org.jgll_staged.lookup.LookupTable
import org.jgll_staged.sppf.NonPackedNode
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.sppf.TerminalSymbolNode
//remove if not needed
import scala.collection.JavaConversions._

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
