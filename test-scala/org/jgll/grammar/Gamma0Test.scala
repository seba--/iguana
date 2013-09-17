package org.jgll.grammar

import org.jgll.util.CollectionsUtil._
import org.junit.Assert._
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.sppf.IntermediateNode
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.PackedNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class Gamma0Test {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("gamma1")
    val r1 = new Rule(new Nonterminal("S"), list(new Character('a'), new Nonterminal("S")))
    builder.addRule(r1)
    val r2 = new Rule(new Nonterminal("S"), list(new Nonterminal("A"), new Nonterminal("S"), new Character('d')))
    builder.addRule(r2)
    val r3 = new Rule(new Nonterminal("S"))
    builder.addRule(r3)
    val r4 = new Rule(new Nonterminal("A"), list(new Character('a')))
    builder.addRule(r4)
    grammar = builder.build()
    levelParser = ParserFactory.levelParser(grammar)
    rdParser = ParserFactory.recursiveDescentParser(grammar)
  }

  @Test
  def testNullables() {
    assertTrue(grammar.getNonterminalByName("S").isNullable)
    assertFalse(grammar.getNonterminalByName("A").isNullable)
  }

  @Test
  def testLongestGrammarChain() {
    assertEquals(1, grammar.getLongestTerminalChain)
  }

  @Test
  def testFirstSets() {
    val s1 = set(new Character('a'), Epsilon)
    val s2 = grammar.getNonterminalByName("S").getFirstSet
    assertEquals(s1, s2)
    assertEquals(set(new Character('a')), grammar.getNonterminalByName("A").getFirstSet)
  }

  @Test
  def testFollowSets() {
    assertEquals(set(new Character('a'), new Character('d'), EOF), grammar.getNonterminalByName("A").getFollowSet)
    assertEquals(set(new Character('d'), EOF), grammar.getNonterminalByName("S").getFollowSet)
  }

  @Test
  def testParsers() {
    val sppf1 = rdParser.parse(Input.fromString("aad"), grammar, "S")
    val sppf2 = levelParser.parse(Input.fromString("aad"), grammar, "S")
    assertTrue(sppf1.deepEquals(sppf2))
  }

  @Test
  def testSPPF() {
    val sppf = rdParser.parse(Input.fromString("aad"), grammar, "S")
    assertTrue(sppf.deepEquals(getSPPF))
  }

  def getSPPF(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3)
    val node2 = new PackedNode(grammar.getGrammarSlotByName("S ::= [a] S ."), 1, node1)
    val node3 = new TerminalSymbolNode(97, 0)
    val node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3)
    val node5 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 1, 2)
    val node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2)
    val node7 = new TerminalSymbolNode(97, 1)
    node6.addChild(node7)
    val node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 2)
    node5.addChild(node6)
    node5.addChild(node8)
    val node10 = new TerminalSymbolNode(100, 2)
    node4.addChild(node5)
    node4.addChild(node10)
    node2.addChild(node3)
    node2.addChild(node4)
    val node11 = new PackedNode(grammar.getGrammarSlotByName("S ::= A S [d] ."), 2, node1)
    val node12 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 0, 2)
    val node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1)
    node13.addChild(node3)
    val node14 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 2)
    node14.addChild(node7)
    node14.addChild(node8)
    node12.addChild(node13)
    node12.addChild(node14)
    node11.addChild(node12)
    node11.addChild(node10)
    node1.addChild(node2)
    node1.addChild(node11)
    node1
  }
}
