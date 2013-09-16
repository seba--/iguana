package org.jgll.sppf

import org.jgll.grammar.slot.GrammarSlot
import org.jgll.traversal.SPPFVisitor
//remove if not needed
import scala.collection.JavaConversions._

class IntermediateNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Int)
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
