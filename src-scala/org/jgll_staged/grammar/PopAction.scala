package org.jgll_staged.grammar

import java.io.Serializable
import org.jgll_staged.parser.GSSEdge
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

trait PopAction extends Serializable {

  def execute(edge: GSSEdge, inputIndex: Int, input: Input): Boolean
}
