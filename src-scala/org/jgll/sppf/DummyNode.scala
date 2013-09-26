package org.jgll.sppf

import org.jgll.grammar.slot.{L0Trait, GrammarSlotTrait}
import org.jgll.traversal.SPPFVisitorTrait
import scala.reflect.BeanProperty
import scala.collection.mutable.ListBuffer

trait DummyNodeTrait {
  self: SPPFNodeTrait
   with SPPFVisitorTrait
   with GrammarSlotTrait
   with L0Trait =>
  object DummyNode extends SPPFNode {

    override def equals(obj: Any): Boolean = obj.isInstanceOf[DummyNode.type]

    override def getLabel(): String = "$"

    override def getLeftExtent(): Int = -1

    override def getRightExtent(): Int = -1

    override def getChildAt(index: Int): SPPFNode = null

    override def childrenCount(): Int = 0

    override def getChildren(): Iterable[SPPFNode] = ListBuffer()

    override def toString(): String = "$"

    override def isAmbiguous(): Boolean = false

    override def accept(visitAction: SPPFVisitor) {
    }

    override def getGrammarSlot(): GrammarSlot = L0

    override def getLevel(): Int = 0
  }
}