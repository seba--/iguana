package org.jgll.grammar.slotaction

import org.jgll.grammar.KeywordTrait
import org.jgll.grammar.slot.BodyGrammarSlotTrait
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.recognizer.RecognizerFactoryTrait
import org.jgll.util.InputTrait
import org.jgll.util.hashing.CuckooHashSet
import scala.collection.mutable.ListBuffer

trait NotMatchActionsTrait {
  self: BodyGrammarSlotTrait
   with SlotActionTrait
   with InputTrait
   with RecognizerFactoryTrait
   with GLLParserInternalsTrait
   with KeywordTrait=>
  object NotMatchActions {

    def fromGrammarSlot(slot: BodyGrammarSlot, ifNot: BodyGrammarSlot) {
      slot.addPopAction(new SlotAction[Boolean]() {

        private val serialVersionUID = 1L

        override def execute(parser: GLLParserInternals, input: Input): Boolean = {
          val recognizer = RecognizerFactory.contextFreeRecognizer()
          recognizer.recognize(input, parser.getCurrentGSSNode.getInputIndex, parser.getCurrentInputIndex, ifNot)
        }
      })
    }

    def fromKeywordList(slot: BodyGrammarSlot, list: ListBuffer[Keyword]) {
      if (list.size == 1) {
        val s = list(0)
        slot.addPopAction(new SlotAction[Boolean]() {

          private val serialVersionUID = 1L

          override def execute(parser: GLLParserInternals, input: Input): Boolean = {
            input.`match`(parser.getCurrentGSSNode.getInputIndex, parser.getCurrentInputIndex, s.chars)
          }
        })
      } else if (list.size == 2) {
        val s1 = list(0)
        val s2 = list(1)
        slot.addPopAction(new SlotAction[Boolean]() {

          private val serialVersionUID = 1L

          override def execute(parser: GLLParserInternals, input: Input): Boolean = {
            val begin = parser.getCurrentGSSNode.getInputIndex
            val end = parser.getCurrentInputIndex
            input.`match`(begin, end, s1.chars) || input.`match`(begin, end, s2.chars)
          }
        })
      } else {
        val set = new CuckooHashSet[Keyword](Keyword.externalHasher)
        for (s <- list) {
          set.add(s)
        }
        slot.addPopAction(new SlotAction[Boolean]() {

          private val serialVersionUID = 1L

          override def execute(parser: GLLParserInternals, input: Input): Boolean = {
            val begin = parser.getCurrentGSSNode.getInputIndex
            val end = parser.getCurrentInputIndex - 1
            val subInput = new Keyword("", input.subInput(begin, end))
            set.contains(subInput)
          }
        })
      }
    }
  }
}