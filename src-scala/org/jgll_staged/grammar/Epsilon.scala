package org.jgll_staged.grammar

import java.util.BitSet
import java.util.Collection
import org.jgll_staged.grammar.condition.Condition
import Epsilon._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object Epsilon {

  private val EPSILON = "epsilon"

  @BeanProperty
  lazy val instance = new Epsilon()
}

@SerialVersionUID(1L)
class Epsilon extends Terminal {

  override def `match`(i: Int): Boolean = true

  override def getMatchCode(): String = ""

  override def toString(): String = getName

  override def getName(): String = EPSILON

  override def asBitSet(): BitSet = new BitSet()

  override def addConditions(condition: Collection[Condition]): Symbol = {
    throw new UnsupportedOperationException()
  }

  override def getConditions(): Collection[Condition] = {
    throw new UnsupportedOperationException()
  }

  override def addCondition(condition: Condition): Symbol = {
    throw new UnsupportedOperationException()
  }
}
