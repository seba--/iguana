package org.jgll.grammar

import org.jgll.util.CollectionsUtil._
import org.jgll.grammar.ebnf.EBNFUtil
import org.jgll.parser.GLLParser
import org.jgll.parser.ParseError
import org.jgll.parser.ParserFactory
import org.jgll.util.Input
import org.junit.Before
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class EBNFTest1 {

  private var grammar: Grammar = _

  private var rdParser: GLLParser = _

  @Before
  def init() {
    val builder = new GrammarBuilder("EBNF")
    val S = new Nonterminal("S")
    val A = new Nonterminal("A")
    val a = new Character('a')
    val rule1 = new Rule(S, list(new Plus(A)))
    val rule2 = new Rule(A, list(a))
    val newRules = EBNFUtil.rewrite(list(rule1, rule2))
    builder.addRules(newRules)
    grammar = builder.build()
    rdParser = ParserFactory.recursiveDescentParser(grammar)
  }

  @Test
  def test() {
    rdParser.parse(Input.fromString("aaa"), grammar, "S")
  }
}
