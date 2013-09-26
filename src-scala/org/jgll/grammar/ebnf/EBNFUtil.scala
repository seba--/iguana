package org.jgll.grammar.ebnf

import org.jgll.grammar.Group
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Opt
import org.jgll.grammar.Plus
import org.jgll.grammar.Rule
import org.jgll.grammar.Symbol
import scala.collection.mutable.ListBuffer

import collection.mutable

object EBNFUtil {

  def isEBNF(s: Symbol): Boolean = {
    s.isInstanceOf[Plus] || s.isInstanceOf[Opt] || s.isInstanceOf[Group]
  }

  def rewrite(rules: Rule*): Iterable[Rule] = rewrite(ListBuffer() ++= (rules:_*))

  def rewrite(rules: Iterable[Rule]): Iterable[Rule] = {
    val set = mutable.Set[Rule]()
    for (rule <- rules) {
      set += (rewrite(rule, set))
    }
    set
  }

  def rewrite(rule: Rule, rules: mutable.Set[Rule]): Rule = {
    val builder = new Rule.Builder(rule.getHead)
    for (s <- rule.getBody) {
      builder.addSymbol(rewrite(s, rules))
    }
    builder.build()
  }

  def rewrite(s: Symbol, rules: mutable.Set[Rule]): Symbol = {
    if (!isEBNF(s)) {
      return s
    }
    if (s.isInstanceOf[Plus]) {
      val in = s.asInstanceOf[Plus].getSymbol
      val newNt = new Nonterminal(s.getName, true)
      rules.add(new Rule(newNt, newNt, in))
      rules.add(new Rule(newNt, in))
      return newNt.addConditions(s.getConditions)
    } else if (s.isInstanceOf[Opt]) {
      val in = s.asInstanceOf[Opt].getSymbol
      val newNt = new Nonterminal(s.getName, true)
      rules.add(new Rule(newNt, in))
      rules.add(new Rule(newNt))
      return newNt.addConditions(s.getConditions)
    } else if (s.isInstanceOf[Group]) {
      val symbols = s.asInstanceOf[Group].getSymbols
      val newNt = new Nonterminal(s.getName, false)
      rules.add(new Rule(newNt, symbols))
      return newNt.addConditions(s.getConditions)
    }
    throw new IllegalStateException("Should not be here!")
  }
}
