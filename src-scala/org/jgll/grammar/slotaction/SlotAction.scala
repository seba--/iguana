package org.jgll.grammar.slotaction

import java.io.Serializable
import org.jgll.parser.GLLParserInternals
import org.jgll.util.Input
//remove if not needed
import scala.collection.JavaConversions._

trait SlotAction[T] extends Serializable {

  def execute(parser: GLLParserInternals, input: Input): T
}
