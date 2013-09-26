package org.jgll.grammar.condition

import org.jgll.grammar.TerminalTrait
import org.jgll.util.CollectionsUtil._
import scala.reflect.BeanProperty
import org.jgll.grammar.condition.ConditionType._
import scala.collection.mutable.ListBuffer

trait TerminalConditionTrait {
  self: TerminalTrait =>
  @SerialVersionUID(1L)
  class TerminalCondition(`type`: ConditionType, @BeanProperty val terminals: ListBuffer[Terminal])
      extends Condition(`type`) {

    override def toString(): String = {
      `type`.toString + " " + listToString(terminals)
    }
  }
}