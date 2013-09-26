package org.jgll.grammar

import org.jgll.grammar.condition.Condition

import scala.collection.mutable._

@SerialVersionUID(1L)
abstract class AbstractSymbol(_conditions: ListBuffer[Condition] = ListBuffer()) extends Symbol {

  val conditions: ListBuffer[Condition] = new ListBuffer() ++= _conditions

  override def getConditions(): ListBuffer[Condition] = conditions

  override def addCondition(condition: Condition): Symbol = {
    addConditions(ListBuffer(condition))
  }
}
