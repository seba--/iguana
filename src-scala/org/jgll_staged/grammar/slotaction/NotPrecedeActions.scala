package org.jgll_staged.grammar.slotaction

import java.util.BitSet
import java.util.List
import org.jgll_staged.grammar.Keyword
import org.jgll_staged.grammar.Terminal
import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.util.Input
import org.jgll_staged.util.logging.LoggerWrapper
//remove if not needed
import scala.collection.JavaConversions._

object NotPrecedeActions {

  private val log = LoggerWrapper.getLogger(NotPrecedeActions.getClass)

  def fromTerminalList(slot: BodyGrammarSlot, terminals: List[Terminal]) {
    log.debug("Precede restriction added %s <<! %s", terminals, slot)
    val testSet = new BitSet()
    for (t <- terminals) {
      testSet.or(t.asBitSet())
    }
    val set = testSet
    slot.addPreCondition(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): Boolean = {
        val ci = parser.getCurrentInputIndex
        if (ci == 0) {
          return false
        }
        set.get(input.charAt(ci - 1))
      }
    })
  }

  def fromKeywordList(slot: BodyGrammarSlot, list: List[Keyword]) {
    log.debug("Precede restriction added %s <<! %s", list, slot)
    slot.addPreCondition(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): Boolean = {
        val ci = parser.getCurrentInputIndex
        if (ci == 0) {
          return false
        }
        for (keyword <- list if input.matchBackward(ci, keyword.chars)) {
          return true
        }
        false
      }
    })
  }
}
