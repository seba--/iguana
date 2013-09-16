package org.jgll.grammar

import java.io.Serializable
import org.jgll.parser.GSSEdge
import org.jgll.util.Input
//remove if not needed
import scala.collection.JavaConversions._

trait PopAction extends Serializable {

  def execute(edge: GSSEdge, inputIndex: Int, input: Input): Boolean
}
