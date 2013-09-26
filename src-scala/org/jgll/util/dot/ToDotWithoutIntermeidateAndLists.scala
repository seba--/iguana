package org.jgll.util.dot

import org.jgll.util.dot.GraphVizUtil.PACKED_NODE
import org.jgll.util.dot.GraphVizUtil.SYMBOL_NODE
import org.jgll.sppf.{ListSymbolNodeTrait, PackedNodeTrait, NonterminalSymbolNodeTrait}
import org.jgll.traversal.SPPFVisitorUtilTrait

trait ToDotWithoutIntermeidateAndListsTrait {
  self: ToDotWithoutIntermediateNodesTrait
   with NonterminalSymbolNodeTrait
   with SPPFVisitorUtilTrait
   with PackedNodeTrait
   with ListSymbolNodeTrait =>
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
}