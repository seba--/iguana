package org.jgll_staged.grammar.slotaction

import java.io.Serializable
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

trait SlotAction[T] extends Serializable {

  def execute(parser: GLLParserInternals, input: Input): T
}
