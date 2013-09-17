package org.jgll.grammar.precedence

import org.junit.Assert._
import org.jgll.util.CollectionsUtil._
import org.jgll.grammar.Character
import org.jgll.grammar.Grammar
import org.jgll.grammar.GrammarBuilder
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Rule
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class FilterTest4 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("TwoLevelFiltering")
    val E = new Nonterminal("E")
    val rule1 = new Rule(E, list(E, new Character('z')))
    builder.addRule(rule1)
    val rule2 = new Rule(E, list(new Character('x'), E))
    builder.addRule(rule2)
    val rule3 = new Rule(E, list(E, new Character('w')))
    builder.addRule(rule3)
    val rule4 = new Rule(E, list(new Character('a')))
    builder.addRule(rule4)
    builder.addPrecedencePattern(E, rule1, 0, rule2)
    builder.addPrecedencePattern(E, rule2, 1, rule3)
    builder.rewritePrecedencePatterns()
    grammar = builder.build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def testAssociativityAndPriority() {
    val sppf1 = rdParser.parse(Input.fromString("xawz"), grammar, "E")
    val sppf2 = levelParser.parse(Input.fromString("xawz"), grammar, "E")
    assertEquals(true, sppf1 == sppf2)
  }
}
