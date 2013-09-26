package org.jgll.grammar

import org.jgll.grammar.slot._

trait GrammarVisitActionTrait {
  self: HeadGrammarSlotTrait
   with NonterminalGrammarSlotTrait
   with TerminalGrammarSlotTrait
   with LastGrammarSlotTrait
   with KeywordGrammarSlotTrait =>
  trait GrammarVisitAction {

    def visit(head: HeadGrammarSlot): Unit

    def visit(slot: NonterminalGrammarSlot): Unit

    def visit(slot: TerminalGrammarSlot): Unit

    def visit(slot: LastGrammarSlot): Unit

    def visit(slot: KeywordGrammarSlot): Unit
  }
}