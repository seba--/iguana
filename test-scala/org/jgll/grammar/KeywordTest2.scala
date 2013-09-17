package org.jgll.grammar

import org.jgll.util.CollectionsUtil._
import org.junit.Assert._
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test

import collection.mutable._

//remove if not needed
import scala.collection.JavaConversions._

class KeywordTest2 {

  private var grammar: Grammar = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val S = new Nonterminal("S")
    val iff = new Keyword("if", Array('i', 'f'))
    val then = new Keyword("then", Array('t', 'h', 'e', 'n'))
    val L = new Nonterminal("L")
    val s = new Character('s')
    val ws = new Character(' ')
    val r1 = new Rule(S, ListBuffer(iff, L, S, L, then, L, S))
    val r2 = new Rule(S, s)
    val r3 = new Rule(L, ws)
    grammar = new GrammarBuilder().addRule(r1).addRule(r2).addRule(r3)
      .addRule(GrammarBuilder.fromKeyword(iff))
      .addRule(GrammarBuilder.fromKeyword(then))
      .build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
  }

  @Test
  def testFirstSet() {
    assertEquals(set(new Character('i'), TerminalFactory.from('s')), grammar.getNonterminalByName("S").getFirstSet)
  }

  @Test
  def testKeywordLength() {
    assertEquals(4, grammar.getLongestTerminalChain)
  }

  @Test
  def test() {
    rdParser.parse(Input.fromString("if s then s"), grammar, "S")
  }
}
