package org.jgll.grammar.precedence

import org.jgll.util.CollectionsUtil._
import org.junit.Assert.assertTrue
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

class FilterTest6 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("TwoLevelFiltering")
    val E = new Nonterminal("E")
    val rule1 = new Rule(E, list(E, new Character('*'), E))
    builder.addRule(rule1)
    val rule2 = new Rule(E, list(E, new Character('+'), E))
    builder.addRule(rule2)
    val rule3 = new Rule(E, list(E, new Character('-'), E))
    builder.addRule(rule3)
    val rule4 = new Rule(E, list(new Character('-'), E))
    builder.addRule(rule4)
    val rule5 = new Rule(E, list(new Character('a')))
    builder.addRule(rule5)
    builder.addPrecedencePattern(E, rule1, 2, rule1)
    builder.addPrecedencePattern(E, rule1, 2, rule2)
    builder.addPrecedencePattern(E, rule1, 0, rule2)
    builder.addPrecedencePattern(E, rule1, 2, rule3)
    builder.addPrecedencePattern(E, rule1, 0, rule4)
    builder.addPrecedencePattern(E, rule1, 0, rule3)
    builder.addPrecedencePattern(E, rule1, 2, rule3)
    builder.addPrecedencePattern(E, rule2, 2, rule2)
    builder.addPrecedencePattern(E, rule3, 2, rule3)
    builder.addPrecedencePattern(E, rule2, 2, rule3)
    builder.addPrecedencePattern(E, rule3, 2, rule2)
    builder.addPrecedencePattern(E, rule2, 0, rule4)
    builder.addPrecedencePattern(E, rule3, 0, rule4)
    builder.rewritePrecedencePatterns()
    grammar = builder.build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def testParsersy() {
    val sppf1 = rdParser.parse(Input.fromString("a+-a+a-a-a+a"), grammar, "E")
    val sppf2 = levelParser.parse(Input.fromString("a+-a+a-a-a+a"), grammar, "E")
    assertTrue(sppf1.deepEquals(sppf2))
  }

  @Test
  def testInput() {
    val sppf = rdParser.parse(Input.fromString("a+a--a+-a+a-a-a+a"), grammar, "E")
    assertTrue(sppf.deepEquals(getSPPF))
  }

  private def getSPPF(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 17)
    val node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E4 [-] . E3"), 0, 4)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 0, 3)
    val node4 = new IntermediateNode(grammar.getGrammarSlotByName("E4 ::= E4 [+] . E2"), 0, 2)
    val node5 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 0, 1)
    val node6 = new TerminalSymbolNode(97, 0)
    node5.addChild(node6)
    val node7 = new TerminalSymbolNode(43, 1)
    node4.addChild(node5)
    node4.addChild(node7)
    val node8 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 2, 3)
    val node9 = new TerminalSymbolNode(97, 2)
    node8.addChild(node9)
    node3.addChild(node4)
    node3.addChild(node8)
    val node10 = new TerminalSymbolNode(45, 3)
    node2.addChild(node3)
    node2.addChild(node10)
    val node11 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 4, 17)
    val node12 = new TerminalSymbolNode(45, 4)
    val node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 5, 17)
    val node14 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E4 [+] . E3"), 5, 7)
    val node15 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 5, 6)
    val node16 = new TerminalSymbolNode(97, 5)
    node15.addChild(node16)
    val node17 = new TerminalSymbolNode(43, 6)
    node14.addChild(node15)
    node14.addChild(node17)
    val node18 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 7, 17)
    val node19 = new TerminalSymbolNode(45, 7)
    val node20 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 8, 17)
    val node21 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E4 [+] . E3"), 8, 16)
    val node22 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 8, 15)
    val node23 = new IntermediateNode(grammar.getGrammarSlotByName("E4 ::= E4 [-] . E2"), 8, 14)
    val node24 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 8, 13)
    val node25 = new IntermediateNode(grammar.getGrammarSlotByName("E4 ::= E4 [-] . E2"), 8, 12)
    val node26 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 8, 11)
    val node27 = new IntermediateNode(grammar.getGrammarSlotByName("E4 ::= E4 [+] . E2"), 8, 10)
    val node28 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 8, 9)
    val node29 = new TerminalSymbolNode(97, 8)
    node28.addChild(node29)
    val node30 = new TerminalSymbolNode(43, 9)
    node27.addChild(node28)
    node27.addChild(node30)
    val node31 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 10, 11)
    val node32 = new TerminalSymbolNode(97, 10)
    node31.addChild(node32)
    node26.addChild(node27)
    node26.addChild(node31)
    val node33 = new TerminalSymbolNode(45, 11)
    node25.addChild(node26)
    node25.addChild(node33)
    val node34 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 12, 13)
    val node35 = new TerminalSymbolNode(97, 12)
    node34.addChild(node35)
    node24.addChild(node25)
    node24.addChild(node34)
    val node36 = new TerminalSymbolNode(45, 13)
    node23.addChild(node24)
    node23.addChild(node36)
    val node37 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 14, 15)
    val node38 = new TerminalSymbolNode(97, 14)
    node37.addChild(node38)
    node22.addChild(node23)
    node22.addChild(node37)
    val node39 = new TerminalSymbolNode(43, 15)
    node21.addChild(node22)
    node21.addChild(node39)
    val node40 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 16, 17)
    val node41 = new TerminalSymbolNode(97, 16)
    node40.addChild(node41)
    node20.addChild(node21)
    node20.addChild(node40)
    node18.addChild(node19)
    node18.addChild(node20)
    node13.addChild(node14)
    node13.addChild(node18)
    node11.addChild(node12)
    node11.addChild(node13)
    node1.addChild(node2)
    node1.addChild(node11)
    node1
  }
}
