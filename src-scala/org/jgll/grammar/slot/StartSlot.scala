package org.jgll.grammar.slot

import java.io.Writer
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.recognizer.GLLRecognizerTrait
import org.jgll.util.InputTrait
//remove if not needed

trait StartSlotTrait {
  self: GLLParserInternalsTrait
   with GLLRecognizerTrait
   with InputTrait
   with GrammarSlotTrait =>

  @SerialVersionUID(1L)
  class StartSlot(label: String) extends GrammarSlot {

    id = -2

    override def codeParser(writer: Writer) {
    }

    override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = null

    override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = null
  }
}