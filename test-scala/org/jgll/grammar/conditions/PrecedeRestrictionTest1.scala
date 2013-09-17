package org.jgll.grammar.conditions

import org.jgll.grammar.condition.ConditionFactory._
import org.jgll.util.CollectionsUtil._
import org.junit.Assert._
import org.jgll.grammar.Character
import org.jgll.grammar.Grammar
import org.jgll.grammar.GrammarBuilder
import org.jgll.grammar.Keyword
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Opt
import org.jgll.grammar.Plus
import org.jgll.grammar.Range
import org.jgll.grammar.Rule
import org.jgll.grammar.Terminal
import org.jgll.grammar.ebnf.EBNFUtil
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.SPPFNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
import scala.collection.mutable.ListBuffer

//remove if not needed
import scala.collection.JavaConversions._

class PrecedeRestrictionTest1 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  @Before
  def init() {
    val S = new Nonterminal("S")
    val forr = new Keyword("for", Array('f', 'o', 'r'))
    val forall = new Keyword("forall", Array('f', 'o', 'r', 'a', 'l', 'l'))
    val L = new Nonterminal("L")
    val Id = new Nonterminal("Id")
    val ws = new Character(' ')
    val az = new Range('a', 'z')
    val builder = new GrammarBuilder()
    val r1 = new Rule(S, ListBuffer(forr, new Opt(L), Id))
    val r2 = new Rule(S, forall)
    val r3 = new Rule(Id, new Plus(az).addCondition(notFollow(az)).addCondition(notPrecede(List(az))))
    val r4 = new Rule(L, ws)
    val rules = EBNFUtil.rewrite(list(r1, r2, r3, r4))
    builder.addRules(rules)
    builder.addRule(GrammarBuilder.fromKeyword(forr))
    builder.addRule(GrammarBuilder.fromKeyword(forall))
    grammar = builder.build()
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def test() {
    val sppf = levelParser.parse(Input.fromString("forall"), grammar, "S")
    assertTrue(sppf.deepEquals(getExpectedSPPF))
  }

  private def getExpectedSPPF(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 6)
    val node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("forall"), 0, 6)
    node1.addChild(node2)
    node1
  }
}
