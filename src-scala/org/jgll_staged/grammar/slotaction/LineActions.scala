package org.jgll_staged.grammar.slotaction

import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

object LineActions {

  def addEndOfLine(slot: BodyGrammarSlot) {
    slot.addPopAction(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): java.lang.Boolean = {
        input.isEndOfLine(parser.getCurrentInputIndex)
      }
    })
  }

  def addStartOfLine(slot: BodyGrammarSlot) {
    slot.addPopAction(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): java.lang.Boolean = {
        input.isStartOfLine(parser.getCurrentInputIndex)
      }
    })
  }
}
