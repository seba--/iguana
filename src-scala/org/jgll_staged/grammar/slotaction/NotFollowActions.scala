package org.jgll_staged.grammar.slotaction

import java.util.BitSet
import java.util.List
import org.jgll_staged.grammar.Keyword
import org.jgll_staged.grammar.Terminal
import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.recognizer.GLLRecognizer
import org.jgll_staged.recognizer.RecognizerFactory
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

object NotFollowActions {

  def fromGrammarSlot(slot: BodyGrammarSlot, firstSlot: BodyGrammarSlot) {
    slot.addPopAction(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): java.lang.Boolean = {
        val recognizer = RecognizerFactory.prefixContextFreeRecognizer()
        recognizer.recognize(input, parser.getCurrentInputIndex, input.size, firstSlot)
      }
    })
  }

  def fromKeywordList(slot: BodyGrammarSlot, list: List[Keyword]) {
    slot.addPopAction(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): java.lang.Boolean = {
        list.find(input.`match`(parser.getCurrentInputIndex, _.getChars))
          .map(true)
          .getOrElse(false)
      }
    })
  }

  def fromTerminalList(slot: BodyGrammarSlot, list: List[Terminal]) {
    val testSet = new BitSet()
    for (t <- list) {
      testSet.or(t.asBitSet())
    }
    val set = testSet
    slot.addPopAction(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): java.lang.Boolean = {
        set.get(input.charAt(parser.getCurrentInputIndex))
      }
    })
  }
}
