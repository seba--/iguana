package org.jgll_staged.grammar.slot

import java.io.IOException
import java.io.Serializable
import java.io.Writer
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.recognizer.GLLRecognizer
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

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
