package org.jgll_staged.grammar

import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.grammar.slot.KeywordGrammarSlot
import org.jgll_staged.grammar.slot.LastGrammarSlot
import org.jgll_staged.grammar.slot.NonterminalGrammarSlot
import org.jgll_staged.grammar.slot.TerminalGrammarSlot
//remove if not needed
import scala.collection.JavaConversions._

object GrammarVisitor {

  def visit(grammar: Grammar, action: GrammarVisitAction) {
    for (head <- grammar.getNonterminals) {
      visit(head, action)
    }
  }

  def visit(heads: java.lang.Iterable[HeadGrammarSlot], action: GrammarVisitAction) {
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
