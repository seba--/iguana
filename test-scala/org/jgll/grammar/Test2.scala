package org.jgll.grammar

import org.junit.Assert._
import org.jgll.util.CollectionsUtil._
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

class Test2 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  private var recognizer: GLLRecognizer = _

  @Before
  def init() {
    val r1 = new Rule(new Nonterminal("A"), list(new Character('a')))
    grammar = new GrammarBuilder("a").addRule(r1).build()
    rdParser = ParserFactory.levelParser(grammar)
    levelParser = ParserFactory.recursiveDescentParser(grammar)
    recognizer = RecognizerFactory.contextFreeRecognizer()
  }

  @Test
  def testLevelParser() {
    val sppf = levelParser.parse(Input.fromString("a"), grammar, "A")
    assertEquals(true, sppf.deepEquals(expectedSPPF()))
  }

  @Test
  def testRDParser() {
    val sppf = rdParser.parse(Input.fromString("a"), grammar, "A")
    assertEquals(true, sppf.deepEquals(expectedSPPF()))
  }

  @Test
  def testRecognizerSuccess() {
    val result = recognizer.recognize(Input.fromString("a"), grammar, "A")
    assertEquals(true, result)
  }

  def testRecognizerFail1() {
    val result = recognizer.recognize(Input.fromString("b"), grammar, "A")
    assertEquals(false, result)
  }

  def testRecognizerFail2() {
    val result = recognizer.recognize(Input.fromString("aa"), grammar, "A")
    assertEquals(false, result)
  }

  private def expectedSPPF(): SPPFNode = {
    val node0 = new TerminalSymbolNode('a', 0)
    val node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 1)
    node1.addChild(node0)
    node1
  }
}
