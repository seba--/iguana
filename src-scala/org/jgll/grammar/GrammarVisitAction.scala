package org.jgll.grammar

import org.jgll.grammar.slot.HeadGrammarSlot
import org.jgll.grammar.slot.KeywordGrammarSlot
import org.jgll.grammar.slot.LastGrammarSlot
import org.jgll.grammar.slot.NonterminalGrammarSlot
import org.jgll.grammar.slot.TerminalGrammarSlot
//remove if not needed
import scala.collection.JavaConversions._

trait GrammarVisitAction {

  def visit(head: HeadGrammarSlot): Unit

  def visit(slot: NonterminalGrammarSlot): Unit

  def visit(slot: TerminalGrammarSlot): Unit

  def visit(slot: LastGrammarSlot): Unit

  def visit(slot: KeywordGrammarSlot): Unit
}
