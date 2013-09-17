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
import org.jgll.sppf.IntermediateNode
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class FilterTest1 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("TwoLevelFiltering")
    val E = new Nonterminal("E")
    val rule1 = new Rule(E, list(E, new Character('+'), E))
    builder.addRule(rule1)
    val rule2 = new Rule(E, list(new Character('-'), E))
    builder.addRule(rule2)
    val rule3 = new Rule(E, list(new Character('a')))
    builder.addRule(rule3)
    builder.addPrecedencePattern(E, rule1, 2, rule1)
    builder.addPrecedencePattern(E, rule1, 0, rule2)
    builder.rewritePrecedencePatterns()
    grammar = builder.build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def testAssociativityAndPriority() {
    val sppf1 = rdParser.parse(Input.fromString("a+-a+a+a"), grammar, "E")
    val sppf2 = levelParser.parse(Input.fromString("a+-a+a+a"), grammar, "E")
    assertTrue(sppf1.deepEquals(sppf2))
  }

  @Test
  def testInput() {
    val sppf = levelParser.parse(Input.fromString("a+-a+a"), grammar, "E")
    assertTrue(sppf.deepEquals(getSPPFNode))
  }

  private def getSPPFNode(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 6)
    val node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 0, 2)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 0, 1)
    val node4 = new TerminalSymbolNode(97, 0)
    node3.addChild(node4)
    val node5 = new TerminalSymbolNode(43, 1)
    node2.addChild(node3)
    node2.addChild(node5)
    val node6 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 2, 6)
    val node7 = new TerminalSymbolNode(45, 2)
    val node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 3, 6)
    val node9 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 3, 5)
    val node10 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 3, 4)
    val node11 = new TerminalSymbolNode(97, 3)
    node10.addChild(node11)
    val node12 = new TerminalSymbolNode(43, 4)
    node9.addChild(node10)
    node9.addChild(node12)
    val node13 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 5, 6)
    val node14 = new TerminalSymbolNode(97, 5)
    node13.addChild(node14)
    node8.addChild(node9)
    node8.addChild(node13)
    node6.addChild(node7)
    node6.addChild(node8)
    node1.addChild(node2)
    node1.addChild(node6)
    node1
  }
}