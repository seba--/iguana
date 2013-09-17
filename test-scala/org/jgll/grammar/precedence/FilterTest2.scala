package org.jgll.grammar.precedence

import org.jgll.grammar.Character
import org.jgll.grammar.Grammar
import org.jgll.grammar.GrammarBuilder
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Rule
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
import org.junit.Assert._
import org.jgll.util.CollectionsUtil._
//remove if not needed
import scala.collection.JavaConversions._

class FilterTest2 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("TwoLevelFiltering")
    val E = new Nonterminal("E")
    val rule0 = new Rule(E, list(E, new Character('^'), E))
    builder.addRule(rule0)
    val rule1 = new Rule(E, list(E, new Character('+'), E))
    builder.addRule(rule1)
    val rule2 = new Rule(E, list(new Character('-'), E))
    builder.addRule(rule2)
    val rule3 = new Rule(E, list(new Character('a')))
    builder.addRule(rule3)
    builder.addPrecedencePattern(E, rule1, 2, rule1)
    builder.addPrecedencePattern(E, rule1, 0, rule2)
    builder.addPrecedencePattern(E, rule0, 0, rule0)
    builder.addPrecedencePattern(E, rule0, 0, rule2)
    builder.addPrecedencePattern(E, rule0, 0, rule1)
    builder.addPrecedencePattern(E, rule0, 2, rule1)
    builder.rewritePrecedencePatterns()
    grammar = builder.build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def testAssociativityAndPriority() {
    val sppf1 = rdParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E")
    val sppf2 = levelParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E")
    assertTrue(sppf1.deepEquals(sppf2))
  }

  @Test
  def testInput() {
    val sppf = levelParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E")
    assertTrue(sppf.deepEquals(getSPPF))
  }

  private def getSPPF(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 10)
    val node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 0, 2)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 0, 1)
    val node4 = new TerminalSymbolNode(97, 0)
    node3.addChild(node4)
    val node5 = new TerminalSymbolNode(43, 1)
    node2.addChild(node3)
    node2.addChild(node5)
    val node6 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 2, 10)
    val node7 = new IntermediateNode(grammar.getGrammarSlotByName("E1 ::= E3 [^] . E1"), 2, 4)
    val node8 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 2, 3)
    val node9 = new TerminalSymbolNode(97, 2)
    node8.addChild(node9)
    val node10 = new TerminalSymbolNode(94, 3)
    node7.addChild(node8)
    node7.addChild(node10)
    val node11 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 4, 10)
    val node12 = new IntermediateNode(grammar.getGrammarSlotByName("E1 ::= E3 [^] . E1"), 4, 6)
    val node13 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 4, 5)
    val node14 = new TerminalSymbolNode(97, 4)
    node13.addChild(node14)
    val node15 = new TerminalSymbolNode(94, 5)
    node12.addChild(node13)
    node12.addChild(node15)
    val node16 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 6, 10)
    val node17 = new TerminalSymbolNode(45, 6)
    val node18 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 7, 10)
    val node19 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 7, 9)
    val node20 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 7, 8)
    val node21 = new TerminalSymbolNode(97, 7)
    node20.addChild(node21)
    val node22 = new TerminalSymbolNode(43, 8)
    node19.addChild(node20)
    node19.addChild(node22)
    val node23 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 9, 10)
    val node24 = new TerminalSymbolNode(97, 9)
    node23.addChild(node24)
    node18.addChild(node19)
    node18.addChild(node23)
    node16.addChild(node17)
    node16.addChild(node18)
    node11.addChild(node12)
    node11.addChild(node16)
    node6.addChild(node7)
    node6.addChild(node11)
    node1.addChild(node2)
    node1.addChild(node6)
    node1
  }
}
