package org.jgll.grammar

import org.junit.Assert._
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.recognizer.GLLRecognizer
import org.jgll.recognizer.RecognizerFactory
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class Test1 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  private var recognizer: GLLRecognizer = _

  @Before
  def init() {
    val r1 = new Rule(new Nonterminal("A"))
    grammar = new GrammarBuilder("epsilon").addRule(r1).build()
    recognizer = RecognizerFactory.contextFreeRecognizer()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
    levelParser = ParserFactory.levelParser(grammar)
  }

  @Test
  def testSPPF() {
    val sppf = rdParser.parse(Input.fromString(""), grammar, "A")
    assertTrue(sppf.deepEquals(expectedSPPF()))
  }

  @Test
  def testParsers() {
    val sppf1 = rdParser.parse(Input.fromString(""), grammar, "A")
    val sppf2 = levelParser.parse(Input.fromString(""), grammar, "A")
    assertEquals(sppf1, sppf2)
  }

  @Test
  def testRecognizerSuccess() {
    assertTrue(recognizer.recognize(Input.fromString(""), grammar, "A"))
  }

  @Test
  def testRecognizerFail() {
    assertFalse(recognizer.recognize(Input.fromString("a"), grammar, "A"))
  }

  private def expectedSPPF(): SPPFNode = {
    val node0 = new TerminalSymbolNode(-2, 0)
    val node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 0)
    node1.addChild(node0)
    node1
  }
}
