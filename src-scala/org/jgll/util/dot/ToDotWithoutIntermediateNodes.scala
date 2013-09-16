package org.jgll.util.dot

import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.PackedNode
import org.jgll.traversal.SPPFVisitor
import org.jgll.traversal.SPPFVisitorUtil
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
