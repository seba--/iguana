package org.jgll_staged.grammar

import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.grammar.slot.KeywordGrammarSlot
import org.jgll_staged.grammar.slot.LastGrammarSlot
import org.jgll_staged.grammar.slot.NonterminalGrammarSlot
import org.jgll_staged.grammar.slot.TerminalGrammarSlot
//remove if not needed
import scala.collection.JavaConversions._

trait GrammarVisitAction {

  def visit(head: HeadGrammarSlot): Unit

  def visit(slot: NonterminalGrammarSlot): Unit

  def visit(slot: TerminalGrammarSlot): Unit

  def visit(slot: LastGrammarSlot): Unit

  def visit(slot: KeywordGrammarSlot): Unit
}
