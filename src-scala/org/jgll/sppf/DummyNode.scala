package org.jgll.sppf

import java.util.Collections
import org.jgll.grammar.slot.GrammarSlot
import org.jgll.grammar.slot.L0
import org.jgll.traversal.SPPFVisitor
import DummyNode._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object DummyNode {

  @BeanProperty
  lazy val instance = new DummyNode()
}

class DummyNode private () extends SPPFNode {

  override def equals(obj: Any): Boolean = obj.isInstanceOf[DummyNode]

  override def getLabel(): String = "$"

  override def getLeftExtent(): Int = -1

  override def getRightExtent(): Int = -1

  override def getChildAt(index: Int): SPPFNode = null

  override def childrenCount(): Int = 0

  override def getChildren(): java.lang.Iterable[SPPFNode] = Collections.emptyList()

  override def toString(): String = "$"

  override def isAmbiguous(): Boolean = false

  override def accept(visitAction: SPPFVisitor) {
  }

  override def getGrammarSlot(): GrammarSlot = L0.getInstance

  override def getLevel(): Int = 0
}
