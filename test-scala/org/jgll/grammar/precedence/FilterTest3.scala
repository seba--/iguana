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

class FilterTest3 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("TwoLevelFiltering")
    val E = new Nonterminal("E")
    val Eplus = new Nonterminal("E+", true)
    val rule1 = new Rule(E, list(E, Eplus))
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
    val sppf = rdParser.parse(Input.fromString("aaa+aaaaa+aaaa"), grammar, "E")
    assertTrue(sppf.deepEquals(getSPPF))
  }

  private def getSPPF(): NonterminalSymbolNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 14)
    val node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E2"), 0, 10)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 9)
    val node4 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E2"), 0, 4)
    val node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 3)
    val node6 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 0, 1)
    val node7 = new TerminalSymbolNode(97, 0)
    node6.addChild(node7)
    val node8 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 1, 3)
    val node9 = new ListSymbolNode(grammar.getNonterminalByName("E+"), 1, 2)
    val node10 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 1, 2)
    val node11 = new TerminalSymbolNode(97, 1)
    node10.addChild(node11)
    node9.addChild(node10)
    val node12 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 2, 3)
    val node13 = new TerminalSymbolNode(97, 2)
    node12.addChild(node13)
    node8.addChild(node9)
    node8.addChild(node12)
    node5.addChild(node6)
    node5.addChild(node8)
    val node14 = new TerminalSymbolNode(43, 3)
    node4.addChild(node5)
    node4.addChild(node14)
    val node15 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 4, 9)
    val node16 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 4, 5)
    val node17 = new TerminalSymbolNode(97, 4)
    node16.addChild(node17)
    val node18 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 5, 9)
    val node19 = new ListSymbolNode(grammar.getNonterminalByName("E+"), 5, 8)
    val node20 = new ListSymbolNode(grammar.getNonterminalByName("E+"), 5, 7)
    val node21 = new ListSymbolNode(grammar.getNonterminalByName("E+"), 5, 6)
    val node22 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 5, 6)
    val node23 = new TerminalSymbolNode(97, 5)
    node22.addChild(node23)
    node21.addChild(node22)
    val node24 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 6, 7)
    val node25 = new TerminalSymbolNode(97, 6)
    node24.addChild(node25)
    node20.addChild(node21)
    node20.addChild(node24)
    val node26 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 7, 8)
    val node27 = new TerminalSymbolNode(97, 7)
    node26.addChild(node27)
    node19.addChild(node20)
    node19.addChild(node26)
    val node28 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 8, 9)
    val node29 = new TerminalSymbolNode(97, 8)
    node28.addChild(node29)
    node18.addChild(node19)
    node18.addChild(node28)
    node15.addChild(node16)
    node15.addChild(node18)
    node3.addChild(node4)
    node3.addChild(node15)
    val node30 = new TerminalSymbolNode(43, 9)
    node2.addChild(node3)
    node2.addChild(node30)
    val node31 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 10, 14)
    val node32 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 10, 11)
    val node33 = new TerminalSymbolNode(97, 10)
    node32.addChild(node33)
    val node34 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 11, 14)
    val node35 = new ListSymbolNode(grammar.getNonterminalByName("E+"), 11, 13)
    val node36 = new ListSymbolNode(grammar.getNonterminalByName("E+"), 11, 12)
    val node37 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 11, 12)
    val node38 = new TerminalSymbolNode(97, 11)
    node37.addChild(node38)
    node36.addChild(node37)
    val node39 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 12, 13)
    val node40 = new TerminalSymbolNode(97, 12)
    node39.addChild(node40)
    node35.addChild(node36)
    node35.addChild(node39)
    val node41 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 13, 14)
    val node42 = new TerminalSymbolNode(97, 13)
    node41.addChild(node42)
    node34.addChild(node35)
    node34.addChild(node41)
    node31.addChild(node32)
    node31.addChild(node34)
    node1.addChild(node2)
    node1.addChild(node31)
    node1
  }
}
