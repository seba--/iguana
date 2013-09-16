package org.jgll.grammar

import org.jgll.grammar.condition.Condition
import java.util

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
abstract class AbstractSymbol(_conditions: Seq[Condition] = Seq()) extends Symbol {

  protected val conditions: Seq[Condition] = new util.ArrayList(_conditions)

  override def getConditions(): Seq[Condition] = conditions

  override def addCondition(condition: Condition): Symbol = {
    addConditions(Seq(condition))
  }
}
