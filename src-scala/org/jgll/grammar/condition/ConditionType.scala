package org.jgll.grammar.condition

//remove if not needed
import scala.collection.JavaConversions._

object ConditionType extends Enumeration {
  type ConditionType = Value

  val FOLLOW = Value(" >> ")

  val NOT_FOLLOW = Value(" !>> ")

  val PRECEDE = Value(" << ")

  val NOT_PRECEDE = Value(" !<< ")

  val MATCH = Value(" & ")

  val NOT_MATCH = Value(" \\ ")

  val END_OF_LINE = Value("$")

  val START_OF_LINE = Value("^")

  implicit def convertValue(v: Value): ConditionType = v.asInstanceOf[ConditionType]
}
