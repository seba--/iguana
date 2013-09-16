package org.jgll.grammar

import java.util.Arrays
import java.util.List
import org.jgll.grammar.condition.Condition
import org.jgll.util.CollectionsUtil
import Group._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object Group {

  def of[T <: Symbol](symbols: T*): Group = new Group(Arrays.asList(symbols:_*))
}

@SerialVersionUID(1L)
class Group(@BeanProperty var symbols: List[_ <: Symbol]) extends Nonterminal("(" + CollectionsUtil.listToString(symbols) + ")", 
  false) {

  override def addCondition(condition: Condition): Group = {
    val group = new Group(this.symbols)
    group.conditions.addAll(this.conditions)
    group.conditions.add(condition)
    group
  }
}
