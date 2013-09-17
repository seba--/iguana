package org.jgll.grammar

import org.junit.Assert._
import org.jgll.util.CollectionsUtil._
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.recognizer.GLLRecognizer
import org.jgll.recognizer.PrefixGLLRecognizer
import org.jgll.recognizer.RecognizerFactory
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class Test3 {

  private var grammar: Grammar = _

  private var levelParser: GLLParser = _

  private var rdParser: GLLParser = _

  private var recognizer: GLLRecognizer = _

  @Before
  def init() {
    val r1 = new Rule(new Nonterminal("A"), list(new Nonterminal("B"), new Nonterminal("C")))
    val r2 = new Rule(new Nonterminal("B"), list(new Character('b')))
    val r3 = new Rule(new Nonterminal("C"), list(new Character('c')))
    grammar = new GrammarBuilder("test3").addRule(r1).addRule(r2)
      .addRule(r3)
      .build()
    rdParser = ParserFactory.levelParser(grammar)
    levelParser = ParserFactory.recursiveDescentParser(grammar)
    recognizer = RecognizerFactory.contextFreeRecognizer()
  }

  @Test
  def testRDParser() {
    val sppf = rdParser.parse(Input.fromString("bc"), grammar, "A")
    assertEquals(true, sppf.deepEquals(expectedSPPF()))
  }

  @Test
  def testLevelParser() {
    val sppf = levelParser.parse(Input.fromString("bc"), grammar, "A")
    assertEquals(true, sppf.deepEquals(expectedSPPF()))
  }

  @Test
  def testRecognizerSuccess() {
    val result = recognizer.recognize(Input.fromString("bc"), grammar, "A")
    assertEquals(true, result)
  }

  @Test
  def testRecognizerFail1() {
    val result = recognizer.recognize(Input.fromString("abc"), grammar, "A")
    assertEquals(false, result)
  }

  @Test
  def testRecognizerFail2() {
    val result = recognizer.recognize(Input.fromString("b"), grammar, "A")
    assertEquals(false, result)
  }

  @Test
  def testRecognizerFail3() {
    val result = recognizer.recognize(Input.fromString("bca"), grammar, "A")
    assertEquals(false, result)
  }

  @Test
  def testPrefixRecognizer() {
    recognizer = new PrefixGLLRecognizer()
    val result = recognizer.recognize(Input.fromString("bca"), grammar, "A")
    assertEquals(true, result)
  }

  private def expectedSPPF(): SPPFNode = {
    val node0 = new TerminalSymbolNode('b', 0)
    val node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1)
    node1.addChild(node0)
    val node2 = new TerminalSymbolNode('c', 1)
    val node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 1, 2)
    node3.addChild(node2)
    val node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 2)
    node4.addChild(node1)
    node4.addChild(node3)
    node4
  }
}
