package org.jgll.grammar

import org.junit.Assert._
import org.jgll.util.CollectionsUtil._
import org.jgll.grammar.condition.ConditionFactory
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.sppf.IntermediateNode
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class DanglingElseGrammar3 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("DanglingElse")
    val S = new Nonterminal("S")
    val s = new Character('s')
    val a = new Character('a')
    val b = new Character('b')
    val rule1 = new Rule(S, list(a, S.addCondition(ConditionFactory.notFollow(b, S))))
    builder.addRule(rule1)
    val rule2 = new Rule(S, list(a, S, b, S))
    builder.addRule(rule2)
    val rule3 = new Rule(S, list(s))
    builder.addRule(rule3)
    grammar = builder.build()
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def test() {
    val sppf = levelParser.parse(Input.fromString("aasbs"), grammar, "S")
    assertEquals(true, sppf.deepEquals(getExpectedSPPF))
  }

  private def getExpectedSPPF(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 5)
    val node2 = new TerminalSymbolNode(97, 0)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 5)
    val node4 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S [b] . S"), 1, 4)
    val node5 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S . [b] S"), 1, 3)
    val node6 = new TerminalSymbolNode(97, 1)
    val node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 3)
    val node8 = new TerminalSymbolNode(115, 2)
    node7.addChild(node8)
    node5.addChild(node6)
    node5.addChild(node7)
    val node9 = new TerminalSymbolNode(98, 3)
    node4.addChild(node5)
    node4.addChild(node9)
    val node10 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 4, 5)
    val node11 = new TerminalSymbolNode(115, 4)
    node10.addChild(node11)
    node3.addChild(node4)
    node3.addChild(node10)
    node1.addChild(node2)
    node1.addChild(node3)
    node1
  }
}
