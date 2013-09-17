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

class DanglingElseGrammar1 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("DanglingElse")
    val S = new Nonterminal("S")
    val s = new Character('s')
    val a = new Character('a')
    val b = new Character('b')
    val rule1 = new Rule(S, list(a, S))
    builder.addRule(rule1)
    val rule2 = new Rule(S, list(Group.of(a, S, b, S).addCondition(ConditionFactory.notMatch(a, S))))
    builder.addRules(EBNFUtil.rewrite(rule2))
    val rule3 = new Rule(S, list(s))
    builder.addRule(rule3)
    grammar = builder.build()
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def test1() {
    val sppf = levelParser.parse(Input.fromString("aasbs"), grammar, "S")
    assertEquals(true, sppf.deepEquals(getExpectedSPPF1))
  }

  @Test
  def test2() {
    val sppf = levelParser.parse(Input.fromString("aaaaasbsbsbs"), grammar, "S")
    assertEquals(true, sppf.deepEquals(getExpectedSPPF2))
  }

  private def getExpectedSPPF1(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 5)
    val node2 = new TerminalSymbolNode(97, 0)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 5)
    val node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("([a] S [b] S )"), 1, 5)
    val node5 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S ) ::= [a] S [b] . S"), 
      1, 4)
    val node6 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S ) ::= [a] S . [b] S"), 
      1, 3)
    val node7 = new TerminalSymbolNode(97, 1)
    val node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 3)
    val node9 = new TerminalSymbolNode(115, 2)
    node8.addChild(node9)
    node6.addChild(node7)
    node6.addChild(node8)
    val node10 = new TerminalSymbolNode(98, 3)
    node5.addChild(node6)
    node5.addChild(node10)
    val node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 4, 5)
    val node12 = new TerminalSymbolNode(115, 4)
    node11.addChild(node12)
    node4.addChild(node5)
    node4.addChild(node11)
    node3.addChild(node4)
    node1.addChild(node2)
    node1.addChild(node3)
    node1
  }

  private def getExpectedSPPF2(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 12)
    val node2 = new TerminalSymbolNode(97, 0)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 12)
    val node4 = new TerminalSymbolNode(97, 1)
    val node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 12)
    val node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("([a] S [b] S )"), 2, 12)
    val node7 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S ) ::= [a] S [b] . S"), 
      2, 11)
    val node8 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S ) ::= [a] S . [b] S"), 
      2, 10)
    val node9 = new TerminalSymbolNode(97, 2)
    val node10 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 3, 10)
    val node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("([a] S [b] S )"), 3, 10)
    val node12 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S ) ::= [a] S [b] . S"), 
      3, 9)
    val node13 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S ) ::= [a] S . [b] S"), 
      3, 8)
    val node14 = new TerminalSymbolNode(97, 3)
    val node15 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 4, 8)
    val node16 = new NonterminalSymbolNode(grammar.getNonterminalByName("([a] S [b] S )"), 4, 8)
    val node17 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S ) ::= [a] S [b] . S"), 
      4, 7)
    val node18 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S ) ::= [a] S . [b] S"), 
      4, 6)
    val node19 = new TerminalSymbolNode(97, 4)
    val node20 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 5, 6)
    val node21 = new TerminalSymbolNode(115, 5)
    node20.addChild(node21)
    node18.addChild(node19)
    node18.addChild(node20)
    val node22 = new TerminalSymbolNode(98, 6)
    node17.addChild(node18)
    node17.addChild(node22)
    val node23 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 7, 8)
    val node24 = new TerminalSymbolNode(115, 7)
    node23.addChild(node24)
    node16.addChild(node17)
    node16.addChild(node23)
    node15.addChild(node16)
    node13.addChild(node14)
    node13.addChild(node15)
    val node25 = new TerminalSymbolNode(98, 8)
    node12.addChild(node13)
    node12.addChild(node25)
    val node26 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 9, 10)
    val node27 = new TerminalSymbolNode(115, 9)
    node26.addChild(node27)
    node11.addChild(node12)
    node11.addChild(node26)
    node10.addChild(node11)
    node8.addChild(node9)
    node8.addChild(node10)
    val node28 = new TerminalSymbolNode(98, 10)
    node7.addChild(node8)
    node7.addChild(node28)
    val node29 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 11, 12)
    val node30 = new TerminalSymbolNode(115, 11)
    node29.addChild(node30)
    node6.addChild(node7)
    node6.addChild(node29)
    node5.addChild(node6)
    node3.addChild(node4)
    node3.addChild(node5)
    node1.addChild(node2)
    node1.addChild(node3)
    node1
  }
}
