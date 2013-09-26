package org.jgll.grammar.slotaction

import java.util.BitSet
import org.jgll.grammar.{TerminalTrait, KeywordTrait}
import org.jgll.grammar.slot.BodyGrammarSlotTrait
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.recognizer.RecognizerFactoryTrait
import org.jgll.util.InputTrait
import scala.collection.mutable.ListBuffer

trait NotFollowActionsTrait {
  self: BodyGrammarSlotTrait
   with SlotActionTrait
   with GLLParserInternalsTrait
   with InputTrait
   with RecognizerFactoryTrait
   with KeywordTrait
   with TerminalTrait
  =>
  object NotFollowActions {

    def fromGrammarSlot(slot: BodyGrammarSlot, firstSlot: BodyGrammarSlot) {
      slot.addPopAction(new SlotAction[Boolean]() {

        private val serialVersionUID = 1L

        override def execute(parser: GLLParserInternals, input: Input): Boolean = {
          val recognizer = RecognizerFactory.prefixContextFreeRecognizer()
          recognizer.recognize(input, parser.getCurrentInputIndex, input.size, firstSlot)
        }
      })
    }

    def fromKeywordList(slot: BodyGrammarSlot, list: ListBuffer[Keyword]) {
      slot.addPopAction(new SlotAction[Boolean]() {

        private val serialVersionUID = 1L

        override def execute(parser: GLLParserInternals, input: Input): Boolean = {
          list.find(x => input.`match`(parser.getCurrentInputIndex, x.chars)).isDefined
        }
      })
    }

    def fromTerminalList(slot: BodyGrammarSlot, list: ListBuffer[Terminal]) {
      val testSet = new BitSet()
      for (t <- list) {
        testSet.or(t.asBitSet())
      }
      val set = testSet
      slot.addPopAction(new SlotAction[Boolean]() {

        private val serialVersionUID = 1L

        override def execute(parser: GLLParserInternals, input: Input): Boolean = {
          set.get(input.charAt(parser.getCurrentInputIndex))
        }
      })
    }
  }
}