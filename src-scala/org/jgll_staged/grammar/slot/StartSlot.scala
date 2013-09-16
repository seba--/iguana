package org.jgll_staged.grammar.slot

import java.io.IOException
import java.io.Writer
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.recognizer.GLLRecognizer
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class StartSlot(label: String) extends GrammarSlot {

  id = -2

  override def codeParser(writer: Writer) {
  }

  override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = null

  override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = null
}
