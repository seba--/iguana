package org.jgll.grammar.slot

import java.io.Serializable
import java.io.Writer
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.recognizer.GLLRecognizerTrait
import org.jgll.util.InputTrait

trait GrammarSlotTrait {
  self: InputTrait
   with GLLParserInternalsTrait
   with GLLRecognizerTrait =>
  @SerialVersionUID(1L)
  abstract class GrammarSlot extends Serializable {

    var id: Int = _

    def codeParser(writer: Writer): Unit

    def parse(parser: GLLParserInternals, input: Input): GrammarSlot

    def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot

    def getId(): Int = id

    def setId(id: Int) {
      this.id = id
    }
  }
}