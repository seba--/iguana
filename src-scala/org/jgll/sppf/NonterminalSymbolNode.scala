package org.jgll.sppf

import org.jgll.grammar.slot.{HeadGrammarSlotTrait, GrammarSlotTrait}
import org.jgll.traversal.SPPFVisitorTrait
import scala.reflect.BooleanBeanProperty
import scala.virtualization.lms.common.Base

//remove if not needed

trait NonterminalSymbolNodeTrait {
  self: GrammarSlotTrait
   with NonPackedNodeTrait
   with SPPFVisitorTrait
   with HeadGrammarSlotTrait
   with Base=>
  class NonterminalSymbolNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Rep[Int])
      extends NonPackedNode(slot, leftExtent, rightExtent) {

    @BooleanBeanProperty
    var keywordNode: Boolean = _

    override def accept(visitAction: SPPFVisitor) {
      visitAction.visit(this)
    }

    override def equals(obj: Any): Boolean = {
      if (!(obj.isInstanceOf[NonterminalSymbolNode])) {
        return false
      }
      super.equals(obj)
    }

    override def getLabel(): String = {
      assert(slot.isInstanceOf[HeadGrammarSlot])
      slot.asInstanceOf[HeadGrammarSlot].getNonterminal.getName
    }
  }
}