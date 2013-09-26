package org.jgll.sppf

import org.jgll.grammar.slot.GrammarSlotTrait
import org.jgll.traversal.SPPFVisitorTrait
import scala.virtualization.lms.common.Base

trait IntermediateNodeTrait {
  self: GrammarSlotTrait with NonPackedNodeTrait with SPPFVisitorTrait with SPPFNodeTrait with Base =>
  class IntermediateNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Rep[Int])
      extends NonPackedNode(slot, leftExtent, rightExtent) {

    override def equals(obj: Any): Boolean = {
      if (!(obj.isInstanceOf[IntermediateNode])) {
        return false
      }
      super.equals(obj)
    }

    override def accept(visitAction: SPPFVisitor) {
      visitAction.visit(this)
    }
  }
}