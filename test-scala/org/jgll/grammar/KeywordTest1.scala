package org.jgll.grammar

import org.jgll.util.CollectionsUtil._
import org.junit.Assert._
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class KeywordTest1 {

  private var grammar: Grammar = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val iff = new Keyword("if", Array('i', 'f'))
    val r1 = new Rule(new Nonterminal("A"), iff)
    val builder = new GrammarBuilder()
    builder.addRule(r1)
    builder.addRule(GrammarBuilder.fromKeyword(iff))
    grammar = builder.build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
  }

  @Test
  def testFirstSet() {
    assertEquals(set(new Character('i')), grammar.getNonterminalByName("A").getFirstSet)
  }

  @Test
  def testKeywordLength() {
    assertEquals(2, grammar.getLongestTerminalChain)
  }

  @Test
  def test() {
    rdParser.parse(Input.fromString("if"), grammar, "A")
  }
}
