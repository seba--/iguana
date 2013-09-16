package org.jgll.util.dot

import org.jgll.util.dot.GraphVizUtil.GSS_EDGE
import org.jgll.util.dot.GraphVizUtil.GSS_NODE
import org.jgll.parser.GSSEdge
import org.jgll.parser.GSSNode
//remove if not needed
import scala.collection.JavaConversions._

class GSSToDot extends ToDot {

  private var sb: StringBuilder = new StringBuilder()

  def execute(set: java.lang.Iterable[GSSNode]) {
    for (node <- set) {
      sb.append("\"" + getId(node) + "\"" + String.format(GSS_NODE, node.toString) + 
        "\n")
      for (edge <- node.getEdges) {
        sb.append(String.format(GSS_EDGE, getId(edge.getSppfNode)) + "\"" + 
          getId(node) + 
          "\"" + 
          "->" + 
          "{\"" + 
          getId(edge.getDestination) + 
          "\"}" + 
          "\n")
      }
    }
  }

  private def getId(node: GSSNode): String = {
    node.getGrammarSlot + "" + node.getInputIndex
  }

  def getString(): String = sb.toString
}
