package org.jgll_staged.grammar.condition

//remove if not needed
import scala.collection.JavaConversions._

object ConditionType extends Enumeration {

  val FOLLOW = new ConditionType(" >> ")

  val NOT_FOLLOW = new ConditionType(" !>> ")

  val PRECEDE = new ConditionType(" << ")

  val NOT_PRECEDE = new ConditionType(" !<< ")

  val MATCH = new ConditionType(" & ")

  val NOT_MATCH = new ConditionType(" \\ ")

  val END_OF_LINE = new ConditionType("$")

  val START_OF_LINE = new ConditionType("^")

  class ConditionType private (private var symbol: String) extends Val {

    override def toString(): String = symbol
  }

  implicit def convertValue(v: Value): ConditionType = v.asInstanceOf[ConditionType]
}
