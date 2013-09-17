package org.jgll.grammar

import java.io.Serializable
import org.jgll.grammar.condition.Condition

import collection.mutable._

//remove if not needed
import scala.collection.JavaConversions._

trait Symbol extends Serializable {

  def getName(): String

  def addCondition(condition: Condition): Symbol

  def addConditions(conditions: Seq[Condition]): Symbol

  def getConditions(): Seq[Condition]
}
