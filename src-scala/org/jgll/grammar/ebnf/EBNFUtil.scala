package org.jgll.grammar.ebnf

import java.util.Arrays
import java.util.HashSet
import java.util.List
import java.util.Set
import org.jgll.grammar.Group
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Opt
import org.jgll.grammar.Plus
import org.jgll.grammar.Rule
import org.jgll.grammar.Symbol
//remove if not needed
import scala.collection.JavaConversions._

object EBNFUtil {

  def isEBNF(s: Symbol): Boolean = {
    s.isInstanceOf[Plus] || s.isInstanceOf[Opt] || s.isInstanceOf[Group]
  }

  def rewrite(rules: Rule*): java.lang.Iterable[Rule] = rewrite(Arrays.asList(rules:_*))

  def rewrite(rules: java.lang.Iterable[Rule]): java.lang.Iterable[Rule] = {
    val set = new HashSet[Rule]()
    for (rule <- rules) {
      set.add(rewrite(rule, set))
    }
    set
  }

  def rewrite(rule: Rule, rules: Set[Rule]): Rule = {
    val builder = new Rule.Builder(rule.getHead)
    for (s <- rule.getBody) {
      builder.addSymbol(rewrite(s, rules))
    }
    builder.build()
  }

  def rewrite(s: Symbol, rules: Set[Rule]): Symbol = {
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
