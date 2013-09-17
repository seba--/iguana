package org.jgll.grammar.conditions

import org.jgll.grammar.condition.ConditionFactory._
import org.jgll.util.CollectionsUtil._
import org.jgll.grammar.Grammar
import org.jgll.grammar.GrammarBuilder
import org.jgll.grammar.Keyword
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Plus
import org.jgll.grammar.Range
import org.jgll.grammar.Rule
import org.jgll.grammar.Terminal
import org.jgll.grammar.ebnf.EBNFUtil
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
import org.junit.rules.ExpectedException
//remove if not needed
import scala.collection.JavaConversions._

class KeywordExclusionTest {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  @Before
  def init() {
    val Id = new Nonterminal("Id")
    val az = new Range('a', 'z')
    val iff = new Keyword("if", "if")
    val when = new Keyword("when", "when")
    val doo = new Keyword("do", "do")
    val whilee = new Keyword("while", "while")
    val builder = new GrammarBuilder()
    val r1 = new Rule(Id, new Plus(az).addCondition(notFollow(az)).addCondition(notMatch(iff, when, doo, 
      whilee)))
    val rules = EBNFUtil.rewrite(list(r1))
    builder.addRules(rules)
    grammar = builder.build()
    levelParser = ParserFactory.levelParser(grammar)
  }

  @org.junit.Rule
  def thrown: ExpectedException = ExpectedException.none()

  @Test
  def testWhen() {
    thrown.expect(classOf[ParseError])
    thrown.expectMessage("Parse error at line:1 column:4")
    levelParser.parse(Input.fromString("when"), grammar, "Id")
  }

  @Test
  def testIf() {
    thrown.expect(classOf[ParseError])
    thrown.expectMessage("Parse error at line:1 column:2")
    levelParser.parse(Input.fromString("if"), grammar, "Id")
  }

  @Test
  def testDo() {
    thrown.expect(classOf[ParseError])
    thrown.expectMessage("Parse error at line:1 column:2")
    levelParser.parse(Input.fromString("do"), grammar, "Id")
  }

  @Test
  def testWhile() {
    thrown.expect(classOf[ParseError])
    thrown.expectMessage("Parse error at line:1 column:5")
    levelParser.parse(Input.fromString("while"), grammar, "Id")
  }
}
