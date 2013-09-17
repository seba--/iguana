package org.jgll.grammar

import org.jgll.util.CollectionsUtil._
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class TestRemovingUnsedNontemrinal {

  private var grammar: Grammar = _

  @Before
  def init() {
    val builder = new GrammarBuilder("TwoLevelFiltering")
    val b = new Character('b')
    val c = new Character('c')
    val d = new Character('d')
    val e = new Character('e')
    val S = new Nonterminal("S")
    val B = new Nonterminal("B")
    val C = new Nonterminal("C")
    val D = new Nonterminal("D")
    val E = new Nonterminal("E")
    builder.addRule(new Rule(S, list(B, C)))
    builder.addRule(new Rule(S, list(D)))
    builder.addRule(new Rule(B, list(b)))
    builder.addRule(new Rule(C, list(c)))
    builder.addRule(new Rule(D, list(d)))
    builder.addRule(new Rule(E, list(e)))
    grammar = builder.removeUnusedNonterminals(S).build()
  }

  @Test
  def test() {
    println(grammar)
  }
}
