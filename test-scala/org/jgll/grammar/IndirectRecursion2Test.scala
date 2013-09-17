package org.jgll.grammar

import org.junit.Assert._
import org.jgll.util.CollectionsUtil._
import java.util.Set
import org.jgll.grammar.slot.HeadGrammarSlot
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

class IndirectRecursion2Test {

  private var builder: GrammarBuilder = _

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  @Before
  def init() {
    val A = new Nonterminal("A")
    val B = new Nonterminal("B")
    val r1 = new Rule(A, list(B, A, new Character('d')))
    val r2 = new Rule(A, list(new Character('a')))
    val r3 = new Rule(B)
    val r4 = new Rule(B, list(new Character('b')))
    builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
      .addRule(r2)
      .addRule(r3)
      .addRule(r4)
    grammar = builder.build()
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def test() {
    val sppf = levelParser.parse(Input.fromString("ad"), grammar, "A")
    assertTrue(sppf.deepEquals(expectedSPPF()))
  }

  @Test
  def testReachabilityGraph() {
    val set = builder.getReachableNonterminals("A")
    assertTrue(set.contains(grammar.getNonterminalByName("A")))
    assertTrue(set.contains(grammar.getNonterminalByName("B")))
  }

  private def expectedSPPF(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 2)
    val node2 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [d]"), 0, 1)
    val node3 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B . A [d]"), 0, 0)
    val node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 0)
    val node5 = new TerminalSymbolNode(-2, 0)
    node4.addChild(node5)
    node3.addChild(node4)
    val node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1)
    val node7 = new TerminalSymbolNode(97, 0)
    node6.addChild(node7)
    node2.addChild(node3)
    node2.addChild(node6)
    val node8 = new TerminalSymbolNode(100, 1)
    node1.addChild(node2)
    node1.addChild(node8)
    node1
  }
}
