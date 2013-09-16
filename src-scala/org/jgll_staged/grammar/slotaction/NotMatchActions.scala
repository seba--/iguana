package org.jgll_staged.grammar.slotaction

import java.util.List
import org.jgll_staged.grammar.Keyword
import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.recognizer.GLLRecognizer
import org.jgll_staged.recognizer.RecognizerFactory
import org.jgll_staged.util.Input
import org.jgll_staged.util.hashing.CuckooHashSet
//remove if not needed
import scala.collection.JavaConversions._

object NotMatchActions {

  def fromGrammarSlot(slot: BodyGrammarSlot, ifNot: BodyGrammarSlot) {
    slot.addPopAction(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): java.lang.Boolean = {
        val recognizer = RecognizerFactory.contextFreeRecognizer()
        recognizer.recognize(input, parser.getCurrentGSSNode.getInputIndex, parser.getCurrentInputIndex, 
          ifNot)
      }
    })
  }

  def fromKeywordList(slot: BodyGrammarSlot, list: List[Keyword]) {
    if (list.size == 1) {
      val s = list.get(0)
      slot.addPopAction(new SlotAction[Boolean]() {

        private val serialVersionUID = 1L

        override def execute(parser: GLLParserInternals, input: Input): java.lang.Boolean = {
          input.`match`(parser.getCurrentGSSNode.getInputIndex, parser.getCurrentInputIndex, s.getChars)
        }
      })
    } else if (list.size == 2) {
      val s1 = list.get(0)
      val s2 = list.get(1)
      slot.addPopAction(new SlotAction[Boolean]() {

        private val serialVersionUID = 1L

        override def execute(parser: GLLParserInternals, input: Input): java.lang.Boolean = {
          val begin = parser.getCurrentGSSNode.getInputIndex
          val end = parser.getCurrentInputIndex
          input.`match`(begin, end, s1.getChars) || input.`match`(begin, end, s2.getChars)
        }
      })
    } else {
      val set = new CuckooHashSet[Keyword](Keyword.externalHasher)
      for (s <- list) {
        set.add(s)
      }
      slot.addPopAction(new SlotAction[Boolean]() {

        private val serialVersionUID = 1L

        override def execute(parser: GLLParserInternals, input: Input): java.lang.Boolean = {
          val begin = parser.getCurrentGSSNode.getInputIndex
          val end = parser.getCurrentInputIndex - 1
          val subInput = new Keyword("", input.subInput(begin, end))
          set.contains(subInput)
        }
      })
    }
  }
}
