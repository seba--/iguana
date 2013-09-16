package org.jgll.grammar.slotaction

import org.jgll.grammar.slot.BodyGrammarSlot
import org.jgll.parser.GLLParserInternals
import org.jgll.util.Input
//remove if not needed
import scala.collection.JavaConversions._

object LineActions {

  def addEndOfLine(slot: BodyGrammarSlot) {
    slot.addPopAction(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): Boolean = {
        input.isEndOfLine(parser.getCurrentInputIndex)
      }
    })
  }

  def addStartOfLine(slot: BodyGrammarSlot) {
    slot.addPopAction(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): Boolean = {
        input.isStartOfLine(parser.getCurrentInputIndex)
      }
    })
  }
}
