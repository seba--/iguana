package org.jgll.grammar

import org.jgll.util.CollectionsUtil._
import org.junit.Assert._
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class LeftFactorizedArithmeticGrammarTest {

  private var grammar: Grammar = _

  @Before
  def init() {
    val builder = new GrammarBuilder("LeftFactorizedArithmeticExpressions")
    val r1 = new Rule(new Nonterminal("E"), list(new Nonterminal("T"), new Nonterminal("E1")))
    val r2 = new Rule(new Nonterminal("E1"), list(new Character('+'), new Nonterminal("T"),
      new Nonterminal("E1")))
    val r3 = new Rule(new Nonterminal("E1"))
    val r4 = new Rule(new Nonterminal("T"), list(new Nonterminal("F"), new Nonterminal("T1")))
    val r5 = new Rule(new Nonterminal("T1"), list(new Character('*'), new Nonterminal("F"),
      new Nonterminal("T1")))
    val r6 = new Rule(new Nonterminal("T1"))
    val r7 = new Rule(new Nonterminal("F"), list(new Character('('), new Nonterminal("E"),
      new Character(')')))
    val r8 = new Rule(new Nonterminal("F"), list(new Character('a')))
    builder.addRule(r1).addRule(r2).addRule(r3).addRule(r4)
      .addRule(r5)
      .addRule(r6)
      .addRule(r7)
      .addRule(r8)
    grammar = builder.build()
  }

  @Test
  def testLongestTerminalChain() {
    assertEquals(1, grammar.getLongestTerminalChain)
  }

  @Test
  def testFirstSets() {
    assertEquals(set(new Character('('), new Character('a')), grammar.getNonterminalByName("E").getFirstSet)
    assertEquals(set(new Character('+'), Epsilon), grammar.getNonterminalByName("E1").getFirstSet)
    assertEquals(set(new Character('*'), Epsilon), grammar.getNonterminalByName("T1").getFirstSet)
    assertEquals(set(new Character('('), new Character('a')), grammar.getNonterminalByName("T").getFirstSet)
    assertEquals(set(new Character('('), new Character('a')), grammar.getNonterminalByName("F").getFirstSet)
  }

  def testFollowSets() {
    assertEquals(set(new Character(')'), EOF), grammar.getNonterminalByName("E").getFollowSet)
    assertEquals(set(new Character(')'), EOF), grammar.getNonterminalByName("E1").getFollowSet)
    assertEquals(set(new Character('+'), new Character(')'), EOF), grammar.getNonterminalByName("T1").getFollowSet)
    assertEquals(set(new Character('+'), new Character(')'), EOF), grammar.getNonterminalByName("T").getFollowSet)
    assertEquals(set(new Character('+'), new Character('*'), new Character(')'),
      EOF), grammar.getNonterminalByName("F").getFollowSet)
  }
}
