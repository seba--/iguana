package org.jgll.grammar.slotaction

import java.io.Serializable
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.util.InputTrait
//remove if not needed

trait SlotActionTrait { self: InputTrait with GLLParserInternalsTrait =>
  trait SlotAction[T] extends Serializable {

    def execute(parser: GLLParserInternals, input: Input): T
  }
}
