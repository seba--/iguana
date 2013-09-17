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
import org.jgll.sppf.ListSymbolNode
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class FilterTest7 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("TwoLevelFiltering")
    val E = new Nonterminal("E")
    val Eplus = new Nonterminal("E+", true)
    val rule1 = new Rule(E, list(Eplus, E))
    builder.addRule(rule1)
    val rule2 = new Rule(E, list(E, new Character('+'), E))
    builder.addRule(rule2)
    val rule3 = new Rule(E, list(new Character('a')))
    builder.addRule(rule3)
    val rule4 = new Rule(Eplus, list(Eplus, E))
    builder.addRule(rule4)
    val rule5 = new Rule(Eplus, list(E))
    builder.addRule(rule5)
    builder.addPrecedencePattern(E, rule1, 0, rule1)
    builder.addPrecedencePattern(E, rule1, 1, rule1)
    builder.addPrecedencePattern(E, rule1, 0, rule2)
    builder.addPrecedencePattern(E, rule1, 1, rule2)
    builder.addPrecedencePattern(E, rule2, 2, rule2)
    builder.addExceptPattern(Eplus, rule4, 1, rule1)
    builder.addExceptPattern(Eplus, rule4, 1, rule2)
    builder.addExceptPattern(Eplus, rule5, 0, rule1)
    builder.addExceptPattern(Eplus, rule5, 0, rule2)
    builder.rewritePatterns()
    grammar = builder.build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
    levelParser = ParserFactory.levelParser(grammar)
  }

  def testParsers() {
    val sppf1 = rdParser.parse(Input.fromString("aaa+aaaa+aaaa"), grammar, "E")
    val sppf2 = levelParser.parse(Input.fromString("aaa+aaaa+aaaa"), grammar, "E")
    assertTrue(sppf1.deepEquals(sppf2))
  }

  @Test
  def testInput() {
    val sppf = rdParser.parse(Input.fromString("aaa+aaaa+aaaa"), grammar, "E")
    assertTrue(sppf.deepEquals(getSPPF))
  }

  private def getSPPF(): NonterminalSymbolNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 13)
    val node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E2"), 0, 9)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 8)
    val node4 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E2"), 0, 4)
    val node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 3)
    val node6 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 0, 2)
    val node7 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 0, 1)
    val node8 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 0, 1)
    val node9 = new TerminalSymbolNode(97, 0)
    node8.addChild(node9)
    node7.addChild(node8)
    val node10 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 1, 2)
    val node11 = new TerminalSymbolNode(97, 1)
    node10.addChild(node11)
    node6.addChild(node7)
    node6.addChild(node10)
    val node12 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 2, 3)
    val node13 = new TerminalSymbolNode(97, 2)
    node12.addChild(node13)
    node5.addChild(node6)
    node5.addChild(node12)
    val node14 = new TerminalSymbolNode(43, 3)
    node4.addChild(node5)
    node4.addChild(node14)
    val node15 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 4, 8)
    val node16 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 4, 7)
    val node17 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 4, 6)
    val node18 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 4, 5)
    val node19 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 4, 5)
    val node20 = new TerminalSymbolNode(97, 4)
    node19.addChild(node20)
    node18.addChild(node19)
    val node21 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 5, 6)
    val node22 = new TerminalSymbolNode(97, 5)
    node21.addChild(node22)
    node17.addChild(node18)
    node17.addChild(node21)
    val node23 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 6, 7)
    val node24 = new TerminalSymbolNode(97, 6)
    node23.addChild(node24)
    node16.addChild(node17)
    node16.addChild(node23)
    val node25 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 7, 8)
    val node26 = new TerminalSymbolNode(97, 7)
    node25.addChild(node26)
    node15.addChild(node16)
    node15.addChild(node25)
    node3.addChild(node4)
    node3.addChild(node15)
    val node27 = new TerminalSymbolNode(43, 8)
    node2.addChild(node3)
    node2.addChild(node27)
    val node28 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 9, 13)
    val node29 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 12)
    val node30 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 11)
    val node31 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 10)
    val node32 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 9, 10)
    val node33 = new TerminalSymbolNode(97, 9)
    node32.addChild(node33)
    node31.addChild(node32)
    val node34 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 10, 11)
    val node35 = new TerminalSymbolNode(97, 10)
    node34.addChild(node35)
    node30.addChild(node31)
    node30.addChild(node34)
    val node36 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 11, 12)
    val node37 = new TerminalSymbolNode(97, 11)
    node36.addChild(node37)
    node29.addChild(node30)
    node29.addChild(node36)
    val node38 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 12, 13)
    val node39 = new TerminalSymbolNode(97, 12)
    node38.addChild(node39)
    node28.addChild(node29)
    node28.addChild(node38)
    node1.addChild(node2)
    node1.addChild(node28)
    node1
  }
}
