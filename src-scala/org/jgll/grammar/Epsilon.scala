package org.jgll.grammar

import java.util.BitSet
import org.jgll.grammar.condition.Condition
import Epsilon._
import scala.reflect.{BeanProperty, BooleanBeanProperty}

import collection.mutable._

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
object Epsilon extends Terminal {
  val EPSILON = "epsilon"

  override def `match`(i: Int): Boolean = true

  override def getMatchCode(): String = ""

  override def toString(): String = getName

  override def getName(): String = EPSILON

  override def asBitSet(): BitSet = new BitSet()

  override def addConditions(condition: ListBuffer[Condition]): Symbol = {
    throw new UnsupportedOperationException()
  }

  override def getConditions(): ListBuffer[Condition] = {
    throw new UnsupportedOperationException()
  }

  override def addCondition(condition: Condition): Symbol = {
    throw new UnsupportedOperationException()
  }
}
