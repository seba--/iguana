package org.jgll.grammar.condition

import org.jgll.grammar.Symbol
import org.jgll.util.CollectionsUtil._
import scala.reflect.BeanProperty
import scala.collection.mutable.ListBuffer

import ConditionType._

@SerialVersionUID(1L)
class ContextFreeCondition(`type`: ConditionType, @BeanProperty val symbols: ListBuffer[Symbol])
    extends Condition(`type`) {

  override def toString(): String = {
    `type`.toString + " " + listToString(symbols)
  }
}
