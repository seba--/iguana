package org.jgll_staged.sppf

import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.traversal.SPPFVisitor
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

class NonterminalSymbolNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Int)
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
