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

class Gamma1Test {

  private var a: Character = new Character('a')

  private var b: Character = new Character('b')

  private var c: Character = new Character('c')

  private var d: Character = new Character('d')

  private var S: Nonterminal = new Nonterminal("S")

  private var A: Nonterminal = new Nonterminal("A")

  private var B: Nonterminal = new Nonterminal("B")

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("gamma1")
    val r1 = new Rule(S, list(A, S, d))
    builder.addRule(r1)
    val r2 = new Rule(S, list(B, S))
    builder.addRule(r2)
    val r3 = new Rule(S)
    builder.addRule(r3)
    val r4 = new Rule(A, list(a))
    builder.addRule(r4)
    val r5 = new Rule(A, list(c))
    builder.addRule(r5)
    val r6 = new Rule(B, list(a))
    builder.addRule(r6)
    val r7 = new Rule(B, list(b))
    builder.addRule(r7)
    grammar = builder.build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def testNullables() {
    assertEquals(true, grammar.getNonterminalByName("S").isNullable)
    assertEquals(false, grammar.getNonterminalByName("A").isNullable)
    assertEquals(false, grammar.getNonterminalByName("B").isNullable)
  }

  @Test
  def testLongestGrammarChain() {
    assertEquals(1, grammar.getLongestTerminalChain)
  }

  @Test
  def testFirstSets() {
    assertEquals(set(a, b, c, Epsilon.getInstance), grammar.getNonterminalByName("S").getFirstSet)
    assertEquals(set(a, c), grammar.getNonterminalByName("A").getFirstSet)
    assertEquals(set(a, b), grammar.getNonterminalByName("B").getFirstSet)
  }

  @Test
  def testFollowSets() {
    assertEquals(set(a, b, c, d, EOF.getInstance), grammar.getNonterminalByName("A").getFollowSet)
    assertEquals(set(d, EOF.getInstance), grammar.getNonterminalByName("S").getFollowSet)
  }

  @Test
  def testParsers() {
    val sppf1 = rdParser.parse(Input.fromString("aad"), grammar, "S")
    val sppf2 = levelParser.parse(Input.fromString("aad"), grammar, "S")
    assertTrue(sppf1.deepEquals(sppf2))
  }

  @Test
  def testSPPF() {
    val sppf = levelParser.parse(Input.fromString("aad"), grammar, "S")
    assertEquals(true, sppf.deepEquals(getSPPF))
  }

  def getSPPF(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3)
    val node2 = new PackedNode(grammar.getGrammarSlotByName("S ::= B S ."), 1, node1)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1)
    val node4 = new TerminalSymbolNode(97, 0)
    node3.addChild(node4)
    val node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3)
    val node6 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 1, 2)
    val node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2)
    val node8 = new TerminalSymbolNode(97, 1)
    node7.addChild(node8)
    val node9 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 2)
    node6.addChild(node7)
    node6.addChild(node9)
    val node11 = new TerminalSymbolNode(100, 2)
    node5.addChild(node6)
    node5.addChild(node11)
    node2.addChild(node3)
    node2.addChild(node5)
    val node12 = new PackedNode(grammar.getGrammarSlotByName("S ::= A S [d] ."), 2, node1)
    val node13 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 0, 2)
    val node14 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1)
    node14.addChild(node4)
    val node15 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 2)
    val node16 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 1, 2)
    node16.addChild(node8)
    node15.addChild(node16)
    node15.addChild(node9)
    node13.addChild(node14)
    node13.addChild(node15)
    node12.addChild(node13)
    node12.addChild(node11)
    node1.addChild(node2)
    node1.addChild(node12)
    node1
  }
}
