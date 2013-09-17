package org.jgll.grammar

import org.junit.Assert._
import org.jgll.util.CollectionsUtil._
import java.util.Set
import org.jgll.grammar.slot.HeadGrammarSlot
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class IndirectRecursion1Test {

  private var builder: GrammarBuilder = _

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  @Before
  def init() {
    val A = new Nonterminal("A")
    val B = new Nonterminal("B")
    val C = new Nonterminal("C")
    val r1 = new Rule(A, list(B, C))
    val r2 = new Rule(A, list(new Character('a')))
    val r3 = new Rule(B, list(A))
    val r4 = new Rule(B, list(new Character('b')))
    val r5 = new Rule(C, list(new Character('c')))
    builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
      .addRule(r2)
      .addRule(r3)
      .addRule(r4)
      .addRule(r5)
    grammar = builder.build()
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def test() {
    val sppf = levelParser.parse(Input.fromString("bc"), grammar, "A")
    assertTrue(sppf.deepEquals(expectedSPPF()))
  }

  @Test
  def testReachabilityGraph() {
    var set = builder.getReachableNonterminals("A")
    assertTrue(set.contains(grammar.getNonterminalByName("A")))
    assertTrue(set.contains(grammar.getNonterminalByName("B")))
    set = builder.getReachableNonterminals("B")
    assertTrue(set.contains(grammar.getNonterminalByName("A")))
  }

  private def expectedSPPF(): SPPFNode = {
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 2)
    val node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1)
    val node3 = new TerminalSymbolNode(98, 0)
    node2.addChild(node3)
    val node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 1, 2)
    val node5 = new TerminalSymbolNode(99, 1)
    node4.addChild(node5)
    node1.addChild(node2)
    node1.addChild(node4)
    node1
  }
}
