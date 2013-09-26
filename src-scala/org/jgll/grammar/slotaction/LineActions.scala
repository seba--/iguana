package org.jgll.grammar.slotaction

import org.jgll.grammar.slot.BodyGrammarSlotTrait
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.util.InputTrait
//remove if not needed

trait LineActionsTrait {
  self: BodyGrammarSlotTrait
   with SlotActionTrait
   with GLLParserInternalsTrait
   with InputTrait =>
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
}