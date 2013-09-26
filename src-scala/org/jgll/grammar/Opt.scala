package org.jgll.grammar

import org.jgll.grammar.condition.Condition

@SerialVersionUID(1L)
class Opt(private var s: Symbol) extends Nonterminal(s.getName + "?") {

  def getSymbol(): Symbol = s

  override def addCondition(condition: Condition): Opt = {
    val opt = new Opt(this.s)
    opt.conditions ++= (this.conditions)
    opt.conditions += (condition)
    opt
  }
}
