package org.jgll_staged.grammar

import org.jgll_staged.grammar.condition.Condition
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
