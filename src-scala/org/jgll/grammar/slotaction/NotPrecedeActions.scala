package org.jgll.grammar.slotaction

import java.util.BitSet
import org.jgll.grammar.{TerminalTrait, KeywordTrait}
import org.jgll.grammar.slot.BodyGrammarSlotTrait
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.util.InputTrait
import org.jgll.util.logging.LoggerWrapper
import scala.collection.mutable.ListBuffer

trait NotPrecedeActionsTrait {
  self: InputTrait
   with BodyGrammarSlotTrait
   with SlotActionTrait
   with GLLParserInternalsTrait
   with KeywordTrait
   with TerminalTrait =>

  object NotPrecedeActions {
    val log = LoggerWrapper.getLogger(NotPrecedeActions.getClass)

    def fromTerminalList(slot: BodyGrammarSlot, terminals: ListBuffer[Terminal]) {
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

    def fromKeywordList(slot: BodyGrammarSlot, list: ListBuffer[Keyword]) {
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
}
