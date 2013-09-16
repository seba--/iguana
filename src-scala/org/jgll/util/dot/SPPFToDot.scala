package org.jgll.util.dot

import org.jgll.util.dot.GraphVizUtil.EDGE
import org.jgll.util.dot.GraphVizUtil.INTERMEDIATE_NODE
import org.jgll.util.dot.GraphVizUtil.PACKED_NODE
import org.jgll.util.dot.GraphVizUtil.SYMBOL_NODE
import org.jgll.sppf.IntermediateNode
import org.jgll.sppf.ListSymbolNode
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.PackedNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.traversal.SPPFVisitorUtil
import org.jgll.traversal.SPPFVisitor
//remove if not needed
import scala.collection.JavaConversions._

class SPPFToDot(private val showPackedNodeLabel: Boolean) extends ToDot with SPPFVisitor {

  protected var sb: StringBuilder = new StringBuilder()

  def this() {
    this(false)
  }

  override def visit(node: TerminalSymbolNode) {
    if (!node.isVisited) {
      node.setVisited(true)
      val label = node.getLabel
      label.replace("ε", "&epsilon;")
      sb.append("\"" + getId(node) + "\"" + 
        String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + 
        "\n")
    }
  }

  override def visit(node: NonterminalSymbolNode) {
    if (!node.isVisited) {
      node.setVisited(true)
      sb.append("\"" + getId(node) + "\"" + 
        String.format(SYMBOL_NODE, replaceWhiteSpace(node.getLabel)) + 
        "\n")
      addEdgesToChildren(node)
      SPPFVisitorUtil.visitChildren(node, this)
    }
  }

  override def visit(node: IntermediateNode) {
    if (!node.isVisited) {
      node.setVisited(true)
      sb.append("\"" + getId(node) + "\"" + 
        String.format(INTERMEDIATE_NODE, replaceWhiteSpace(node.toString)) + 
        "\n")
      addEdgesToChildren(node)
      SPPFVisitorUtil.visitChildren(node, this)
    }
  }

  override def visit(node: PackedNode) {
    if (!node.isVisited) {
      node.setVisited(true)
      if (showPackedNodeLabel) {
        sb.append("\"" + getId(node) + "\"" + 
          String.format(PACKED_NODE, replaceWhiteSpace(node.toString)) + 
          "\n")
      } else {
        sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + 
          "\n")
      }
      addEdgesToChildren(node)
      SPPFVisitorUtil.visitChildren(node, this)
    }
  }

  protected def addEdgesToChildren(node: SPPFNode) {
    for (child <- node.getChildren) {
      addEdgeToChild(node, child)
    }
  }

  protected def addEdgeToChild(parentNode: SPPFNode, childNode: SPPFNode) {
    sb.append(EDGE + "\"" + getId(parentNode) + "\"" + "->" + "{\"" + 
      getId(childNode) + 
      "\"}" + 
      "\n")
  }

  protected def replaceWhiteSpace(s: String): String = {
    s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n")
      .replace("\r", "\\\\r")
      .replace("\"", "\\\"")
  }

  override def visit(node: ListSymbolNode) {
    visit(node.asInstanceOf[NonterminalSymbolNode])
  }

  def getString(): String = sb.toString
}
