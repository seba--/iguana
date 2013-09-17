package org.jgll.grammar

import org.junit.Assert._
import org.jgll.util.CollectionsUtil._
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class Gamma2Test {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("gamma2")
    val rule1 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S"), new Nonterminal("S")))
    builder.addRule(rule1)
    val rule2 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S")))
    builder.addRule(rule2)
    val rule3 = new Rule(new Nonterminal("S"), list(new Character('b')))
    builder.addRule(rule3)
    grammar = builder.build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def testLongestTerminalChain() {
    assertEquals(1, grammar.getLongestTerminalChain)
  }

  @Test
  def testParsers() {
    val sppf1 = rdParser.parse(Input.fromString("bbb"), grammar, "S")
    val sppf2 = levelParser.parse(Input.fromString("bbb"), grammar, "S")
    assertEquals(true, sppf1.deepEquals(sppf2))
  }

  @Test
  def test100bs() {
    val input = Input.fromString(get100b)
    levelParser.parse(input, grammar, "S")
  }

  private def get100b(): String = {
    val sb = new StringBuilder()
    for (i <- 0 until 100) {
      sb.append("b")
    }
    sb.toString
  }
}
