package org.jgll.util.dot

import org.jgll.sppf.{NonterminalSymbolNodeTrait, PackedNodeTrait}
import org.jgll.traversal.SPPFVisitorUtilTrait

trait ToDotWithoutIntermediateNodesTrait {
  self: SPPFToDotTrait
   with PackedNodeTrait
   with SPPFVisitorUtilTrait
   with NonterminalSymbolNodeTrait =>
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
}