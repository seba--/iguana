package org.jgll_staged.grammar

import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.List
import org.jgll_staged.grammar.condition.Condition
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
abstract class AbstractSymbol(conditions: java.lang.Iterable[Condition]) extends Symbol {

  protected val conditions = new ArrayList()

  for (condition <- conditions) {
    this.conditions.add(condition)
  }

  def this() {
    this()
    this.conditions = new ArrayList()
  }

  override def getConditions(): Collection[Condition] = conditions

  override def addCondition(condition: Condition): Symbol = {
    addConditions(Arrays.asList(condition:_*))
  }
}
