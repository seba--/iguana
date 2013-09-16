package org.jgll.grammar.condition

import ConditionType._
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class PositionalCondition(`type`: ConditionType) extends Condition(`type`) {

  override def toString(): String = `type`.toString
}
