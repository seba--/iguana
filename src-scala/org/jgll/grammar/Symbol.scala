package org.jgll.grammar

import java.io.Serializable
import org.jgll.grammar.condition.Condition

import collection.mutable._

trait Symbol extends Serializable {

  def getName(): String

  def addCondition(condition: Condition): Symbol

  def addConditions(conditions: ListBuffer[Condition]): Symbol

  def getConditions(): ListBuffer[Condition]
}
