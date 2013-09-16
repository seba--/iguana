package org.jgll_staged.grammar

import org.jgll_staged.grammar.condition.Condition
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class Opt(private var s: Symbol) extends Nonterminal(s.getName + "?") {

  def getSymbol(): Symbol = s

  override def addCondition(condition: Condition): Opt = {
    val opt = new Opt(this.s)
    opt.conditions.addAll(this.conditions)
    opt.conditions.add(condition)
    opt
  }
}
