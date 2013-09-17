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

class FollowRestrictionTest {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  @Before
  def init() {
    val S = new Nonterminal("S")
    val Label = new Nonterminal("Label")
    val az = new Range('a', 'z')
    val builder = new GrammarBuilder()
    val r1 = new Rule(S, Label.addCondition(notFollow(new Keyword(":", Array(':')))))
    val r2 = new Rule(Label, new Plus(az).addCondition(notFollow(az)))
    val rules = EBNFUtil.rewrite(list(r1, r2))
    builder.addRules(rules)
    grammar = builder.build()
    levelParser = ParserFactory.levelParser(grammar)
  }

  @org.junit.Rule
  def thrown: ExpectedException = ExpectedException.none()

  @Test
  def test() {
    thrown.expect(classOf[ParseError])
    thrown.expectMessage("Parse error at line:1 column:4")
    levelParser.parse(Input.fromString("abc:"), grammar, "S")
  }
}
