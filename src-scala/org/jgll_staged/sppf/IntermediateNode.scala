package org.jgll_staged.sppf

import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.traversal.SPPFVisitor
//remove if not needed
import scala.collection.JavaConversions._

class IntermediateNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Int)
    extends NonPackedNode(slot, leftExtent, rightExtent) {

  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[IntermediateNode])) {
      return false
    }
    super == obj
  }

  override def accept(visitAction: SPPFVisitor) {
    visitAction.visit(this)
  }
}
