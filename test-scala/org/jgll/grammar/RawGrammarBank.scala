package org.jgll.grammar

import org.jgll.util.CollectionsUtil._
//remove if not needed
import scala.collection.JavaConversions._

object RawGrammarBank {

  def arithmeticExpressions(): GrammarBuilder = {
    val builder = new GrammarBuilder("ArithmeticExpressions")
    val E = new Nonterminal("E")
    val rule1 = new Rule(E, list(E, new Character('^'), E))
    builder.addRule(rule1)
    val rule2 = new Rule(E, list(new Character('-'), E))
    builder.addRule(rule2)
    val rule3 = new Rule(E, list(E, new Character('*'), E))
    builder.addRule(rule3)
    val rule4 = new Rule(E, list(E, new Character('/'), E))
    builder.addRule(rule4)
    val rule5 = new Rule(E, list(E, new Character('+'), E))
    builder.addRule(rule5)
    val rule6 = new Rule(E, list(E, new Character('-'), E))
    builder.addRule(rule6)
    val rule7 = new Rule(E, list(new Character('a')))
    builder.addRule(rule7)
    builder
  }
}
