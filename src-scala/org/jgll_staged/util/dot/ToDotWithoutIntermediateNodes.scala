package org.jgll_staged.util.dot

import org.jgll_staged.sppf.NonterminalSymbolNode
import org.jgll_staged.sppf.PackedNode
import org.jgll_staged.traversal.SPPFVisitor
import org.jgll_staged.traversal.SPPFVisitorUtil
//remove if not needed
import scala.collection.JavaConversions._

class ToDotWithoutIntermediateNodes extends SPPFToDot {

  override def visit(node: PackedNode) {
    SPPFVisitorUtil.removeIntermediateNode(node)
    super.visit(node)
  }

  override def visit(node: NonterminalSymbolNode) {
    SPPFVisitorUtil.removeIntermediateNode(node)
    super.visit(node)
  }
}
