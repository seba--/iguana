package org.jgll_staged.grammar.grammaraction

import org.jgll_staged.grammar.GrammarVisitAction
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.grammar.slot.KeywordGrammarSlot
import org.jgll_staged.grammar.slot.LastGrammarSlot
import org.jgll_staged.grammar.slot.NonterminalGrammarSlot
import org.jgll_staged.grammar.slot.TerminalGrammarSlot
//remove if not needed
import scala.collection.JavaConversions._

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
