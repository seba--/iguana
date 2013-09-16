package org.jgll_staged.sppf

import java.util.Iterator
import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.traversal.SPPFVisitor
import org.jgll_staged.util.hashing.Level
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

abstract class SPPFNode extends Level {

  @BooleanBeanProperty
  var visited: Boolean = _

  @BeanProperty
  var `object`: AnyRef = _

  def getLabel(): String

  def getChildAt(index: Int): SPPFNode

  def getChildren(): java.lang.Iterable[SPPFNode]

  def isAmbiguous(): Boolean

  def childrenCount(): Int

  def getLeftExtent(): Int

  def getRightExtent(): Int

  def accept(visitAction: SPPFVisitor): Unit

  def getGrammarSlot(): GrammarSlot

  def deepEquals(node: SPPFNode): Boolean = {
    if (this != node) {
      return false
    }
    if (this.childrenCount() != node.childrenCount()) {
      return false
    }
    if (this.isAmbiguous ^ node.isAmbiguous) {
      return false
    }
    if (this.isAmbiguous && node.isAmbiguous) {
      val thisIt = getChildren.iterator()
      outer: while (thisIt.hasNext) {
        val thisChild = thisIt.next()
        val otherIt = node.getChildren.iterator()
        while (otherIt.hasNext) {
          val otherChild = otherIt.next()
          if (thisChild.deepEquals(otherChild)) {
            //continue
          }
        }
        return false
      }
      return true
    }
    val thisIt = getChildren.iterator()
    val otherIt = node.getChildren.iterator()
    while (thisIt.hasNext && otherIt.hasNext) {
      val thisChild = thisIt.next()
      val otherChild = otherIt.next()
      if (!thisChild.deepEquals(otherChild)) {
        return false
      }
    }
    true
  }
}
