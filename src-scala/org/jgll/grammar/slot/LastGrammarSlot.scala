package org.jgll.grammar.slot

import java.io.Writer
import org.jgll.grammar.Symbol
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.recognizer.GLLRecognizerTrait
import scala.reflect.BeanProperty
import org.jgll.util.InputTrait

trait LastGrammarSlotTrait {
  self: BodyGrammarSlotTrait
   with HeadGrammarSlotTrait
   with InputTrait
   with GrammarSlotTrait
   with GLLParserInternalsTrait
   with GLLRecognizerTrait =>
  @SerialVersionUID(1L)
  class LastGrammarSlot(position: Int,
      previous: BodyGrammarSlot,
      head: HeadGrammarSlot,
      @BeanProperty var obj: AnyRef) extends BodyGrammarSlot(position, previous, head) {

    def copy(previous: BodyGrammarSlot, head: HeadGrammarSlot): LastGrammarSlot = {
      val slot = new LastGrammarSlot(this.position, previous, head, obj)
      slot.preConditions = preConditions
      slot.popActions = popActions
      slot
    }

    override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = {
      parser.pop()
      null
    }

    override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = {
      recognizer.pop(recognizer.getCu, recognizer.getCi)
      null
    }

    override def codeParser(writer: Writer) {
      writer.append("   pop(cu, ci, cn);\n")
      writer.append("   label = L0;\n}\n")
    }

    override def testFirstSet(index: Int, input: Input): Boolean = {
      throw new UnsupportedOperationException()
    }

    override def testFollowSet(index: Int, input: Input): Boolean = {
      throw new UnsupportedOperationException()
    }

    override def codeIfTestSetCheck(writer: Writer) {
      throw new UnsupportedOperationException()
    }

    override def isNullable(): Boolean = false

    override def getSymbol(): Symbol = {
      throw new UnsupportedOperationException()
    }

    override def isNameEqual(slot: BodyGrammarSlot): Boolean = {
      throw new UnsupportedOperationException()
    }
  }
}