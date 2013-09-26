package org.jgll.grammar

import org.jgll.grammar.slot._
import scala.collection.mutable.ListBuffer

trait GrammarVisitorTrait {
  self: GrammarTrait
   with GrammarVisitActionTrait
   with HeadGrammarSlotTrait
   with LastGrammarSlotTrait
   with NonterminalGrammarSlotTrait
   with TerminalGrammarSlotTrait
   with KeywordGrammarSlotTrait =>
  object GrammarVisitor {

    def visit(grammar: Grammar, action: GrammarVisitAction) {
      for (head <- grammar.getNonterminals) {
        visit(head, action)
      }
    }

    def visit(heads: ListBuffer[HeadGrammarSlot], action: GrammarVisitAction) {
      for (head <- heads) {
        visit(head, action)
      }
    }

    def visit(root: HeadGrammarSlot, action: GrammarVisitAction) {
      action.visit(root)
      for (alternate <- root.getAlternates) {
        var currentSlot = alternate.getFirstSlot
        while (!(currentSlot.isInstanceOf[LastGrammarSlot])) {
          if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
            action.visit(currentSlot.asInstanceOf[NonterminalGrammarSlot])
          } else if (currentSlot.isInstanceOf[TerminalGrammarSlot]) {
            action.visit(currentSlot.asInstanceOf[TerminalGrammarSlot])
          } else if (currentSlot.isInstanceOf[KeywordGrammarSlot]) {
            action.visit(currentSlot.asInstanceOf[KeywordGrammarSlot])
          }
          currentSlot = currentSlot.next()
        }
        action.visit(currentSlot.asInstanceOf[LastGrammarSlot])
      }
    }
  }
}