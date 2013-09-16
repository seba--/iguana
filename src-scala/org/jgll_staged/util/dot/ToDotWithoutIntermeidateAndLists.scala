package org.jgll_staged.util.dot

import org.jgll_staged.util.dot.GraphVizUtil.PACKED_NODE
import org.jgll_staged.util.dot.GraphVizUtil.SYMBOL_NODE
import org.jgll_staged.sppf.ListSymbolNode
import org.jgll_staged.sppf.NonterminalSymbolNode
import org.jgll_staged.sppf.PackedNode
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.traversal.SPPFVisitorUtil
//remove if not needed
import scala.collection.JavaConversions._

class ToDotWithoutIntermeidateAndLists extends ToDotWithoutIntermediateNodes {

  override def visit(node: NonterminalSymbolNode) {
    SPPFVisitorUtil.removeIntermediateNode(node)
    if (!node.isVisited) {
      node.setVisited(true)
      sb.append("\"" + getId(node) + "\"" + 
        String.format(SYMBOL_NODE, replaceWhiteSpace(node.toString)) + 
        "\n")
      for (child <- node.getChildren if !child.getLabel.startsWith("layout")) {
        addEdgeToChild(node, child)
        child.accept(this)
      }
    }
  }

  override def visit(node: PackedNode) {
    SPPFVisitorUtil.removeIntermediateNode(node)
    if (!node.isVisited) {
      node.setVisited(true)
      sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + 
        "\n")
      for (child <- node.getChildren if !child.getLabel.startsWith("layout")) {
        addEdgeToChild(node, child)
        child.accept(this)
      }
    }
  }

  override def visit(node: ListSymbolNode) {
    SPPFVisitorUtil.removeListSymbolNode(node)
    visit(node.asInstanceOf[NonterminalSymbolNode])
  }
}
