package org.jgll.parser

import org.jgll.grammar.Keyword
import org.jgll.grammar.slot.BodyGrammarSlot
import org.jgll.grammar.slot.GrammarSlot
import org.jgll.grammar.slot.HeadGrammarSlot
import org.jgll.grammar.slot.LastGrammarSlot
import org.jgll.lookup.LookupTable
import org.jgll.sppf.NonPackedNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
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
