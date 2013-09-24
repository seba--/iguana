package org.jgll.grammar.slotaction

import java.util.BitSet
import java.util.List
import org.jgll.grammar.Keyword
import org.jgll.grammar.Terminal
import org.jgll.grammar.slot.BodyGrammarSlot
import org.jgll.parser.GLLParserInternals
import org.jgll.util.{InputTrait, Input}
import org.jgll.util.logging.LoggerWrapper
//remove if not needed
import scala.collection.JavaConversions._

trait NotPrecedeActionsTrait extends InputTrait {

  import NotPrecedeActions._
  object NotPrecedeActions {
    val log = LoggerWrapper.getLogger(NotPrecedeActions.getClass)
  }

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
