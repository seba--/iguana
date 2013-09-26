package org.jgll.sppf

import org.jgll.grammar.slot.GrammarSlotTrait
import org.jgll.traversal.SPPFVisitorTrait
import scala.virtualization.lms.common.Base

trait ListSymbolNodeTrait {
  self: GrammarSlotTrait
   with SPPFVisitorTrait
   with NonterminalSymbolNodeTrait
   with Base =>
  class ListSymbolNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Rep[Int]) extends NonterminalSymbolNode(slot,
    leftExtent, rightExtent) {

    override def accept(visitAction: SPPFVisitor) {
      visitAction.visit(this)
    }
  }
}