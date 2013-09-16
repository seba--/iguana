package org.jgll_staged.grammar

import java.io.Serializable
import java.util.Collection
import org.jgll_staged.grammar.condition.Condition
//remove if not needed
import scala.collection.JavaConversions._

trait Symbol extends Serializable {

  def getName(): String

  def addCondition(condition: Condition): Symbol

  def addConditions(conditions: Collection[Condition]): Symbol

  def getConditions(): Collection[Condition]
}
