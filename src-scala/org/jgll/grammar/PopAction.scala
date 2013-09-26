package org.jgll.grammar

import java.io.Serializable
import org.jgll.util.InputTrait
import org.jgll.parser.GSSEdgeTrait

trait PopActionTrait {
  self: InputTrait
   with GSSEdgeTrait =>
  trait PopAction extends Serializable {

    def execute(edge: GSSEdge, inputIndex: Int, input: Input): Boolean
  }
}