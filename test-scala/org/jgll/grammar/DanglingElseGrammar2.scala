package org.jgll.grammar

import org.jgll.util.CollectionsUtil._
import org.junit.Assert._
import org.jgll.grammar.condition.ConditionFactory
import org.jgll.grammar.ebnf.EBNFUtil
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

class DanglingElseGrammar2 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("DanglingElse")
    val S = new Nonterminal("S")
    val s = new Character('s')
    val a = new Character('a')
    val b = new Character('b')
    val rule1 = new Rule(S, list(Group.of(a, S).addCondition(ConditionFactory.notMatch(a, S, b, S))))
    builder.addRules(EBNFUtil.rewrite(rule1))
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
    val node2 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S [b] . S"), 0, 4)
    val node3 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S . [b] S"), 0, 3)
    val node4 = new TerminalSymbolNode(97, 0)
    val node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3)
    val node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("([a] S )"), 1, 3)
    val node7 = new TerminalSymbolNode(97, 1)
    val node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 3)
    val node9 = new TerminalSymbolNode(115, 2)
    node8.addChild(node9)
    node6.addChild(node7)
    node6.addChild(node8)
    node5.addChild(node6)
    node3.addChild(node4)
    node3.addChild(node5)
    val node10 = new TerminalSymbolNode(98, 3)
    node2.addChild(node3)
    node2.addChild(node10)
    val node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 4, 5)
    val node12 = new TerminalSymbolNode(115, 4)
    node11.addChild(node12)
    node1.addChild(node2)
    node1.addChild(node11)
    node1
  }
}
