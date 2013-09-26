package org.jgll.util.dot

import org.jgll.util.dot.GraphVizUtil.EDGE
import org.jgll.util.dot.GraphVizUtil.INTERMEDIATE_NODE
import org.jgll.util.dot.GraphVizUtil.PACKED_NODE
import org.jgll.util.dot.GraphVizUtil.SYMBOL_NODE
import org.jgll.sppf._
import org.jgll.traversal.{SPPFVisitorUtilTrait, SPPFVisitorTrait}

trait SPPFToDotTrait {
  self: ToDotTrait
   with SPPFVisitorTrait
   with TerminalSymbolNodeTrait
   with NonterminalSymbolNodeTrait
   with IntermediateNodeTrait
   with PackedNodeTrait
   with SPPFNodeTrait
   with ListSymbolNodeTrait
   with SPPFVisitorUtilTrait =>


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
}