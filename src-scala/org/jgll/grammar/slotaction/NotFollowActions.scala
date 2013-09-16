package org.jgll.grammar.slotaction

import java.util.BitSet
import java.util.List
import org.jgll.grammar.Keyword
import org.jgll.grammar.Terminal
import org.jgll.grammar.slot.BodyGrammarSlot
import org.jgll.parser.GLLParserInternals
import org.jgll.recognizer.GLLRecognizer
import org.jgll.recognizer.RecognizerFactory
import org.jgll.util.Input
//remove if not needed
import scala.collection.JavaConversions._

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

  def fromKeywordList(slot: BodyGrammarSlot, list: List[Keyword]) {
    slot.addPopAction(new SlotAction[Boolean]() {

      private val serialVersionUID = 1L

      override def execute(parser: GLLParserInternals, input: Input): Boolean = {
        list.find(x => input.`match`(parser.getCurrentInputIndex, x.chars)).isDefined
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

      override def execute(parser: GLLParserInternals, input: Input): Boolean = {
        set.get(input.charAt(parser.getCurrentInputIndex))
      }
    })
  }
}
