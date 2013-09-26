package org.jgll.grammar

import org.jgll.grammar.condition.Condition

@SerialVersionUID(1L)
class Plus(private var s: Symbol) extends Nonterminal(s.getName + "+") {

  def getSymbol(): Symbol = s

  override def addCondition(condition: Condition): Plus = {
    val plus = new Plus(this.s)
    plus.conditions ++= (this.conditions)
    plus.conditions += (condition)
    plus
  }
}
