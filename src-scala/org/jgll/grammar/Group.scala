package org.jgll.grammar

import org.jgll.grammar.condition.Condition
import org.jgll.util.CollectionsUtil
import scala.reflect.{BeanProperty, BooleanBeanProperty}

import collection.mutable._

object Group {

  def of[T <: Symbol](symbols: T*): Group = new Group(ListBuffer() ++= symbols)
}

@SerialVersionUID(1L)
class Group(@BeanProperty val symbols: ListBuffer[Symbol]) extends Nonterminal("(" + CollectionsUtil.listToString(symbols) + ")",
  false) {

  override def addCondition(condition: Condition): Group = {
    val group = new Group(this.symbols)
    group.conditions ++= (this.conditions)
    group.conditions += (condition)
    group
  }
}
