package org.jgll.grammar.grammaraction

import org.jgll.grammar.{GrammarVisitActionTrait, GrammarVisitorTrait}
import org.jgll.grammar.slot._

trait LongestTerminalChainActionTrait {
  self: GrammarVisitorTrait
   with KeywordGrammarSlotTrait
   with GrammarVisitorTrait
   with LastGrammarSlotTrait
   with TerminalGrammarSlotTrait
   with NonterminalGrammarSlotTrait
   with GrammarVisitActionTrait
   with HeadGrammarSlotTrait =>
  class LongestTerminalChainAction extends GrammarVisitAction {

    private var longestTerminalChain: Int = _

    private var length: Int = _

    def visit(slot: KeywordGrammarSlot) {
      length += slot.getKeyword.size
    }

    override def visit(slot: LastGrammarSlot) {
      if (length > longestTerminalChain) {
        longestTerminalChain = length
      }
      length = 0
    }

    override def visit(slot: TerminalGrammarSlot) {
      length += 1
    }

    override def visit(slot: NonterminalGrammarSlot) {
      if (length > longestTerminalChain) {
        longestTerminalChain = length
      }
      length = 0
    }

    override def visit(head: HeadGrammarSlot) {
    }

    def getLongestTerminalChain(): Int = {
      if (longestTerminalChain == 0) 1 else longestTerminalChain
    }
  }
}