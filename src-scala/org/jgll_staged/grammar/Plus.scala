package org.jgll_staged.grammar

import org.jgll_staged.grammar.condition.Condition
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class Plus(private var s: Symbol) extends Nonterminal(s.getName + "+") {

  def getSymbol(): Symbol = s

  override def addCondition(condition: Condition): Plus = {
    val plus = new Plus(this.s)
    plus.conditions.addAll(this.conditions)
    plus.conditions.add(condition)
    plus
  }
}
