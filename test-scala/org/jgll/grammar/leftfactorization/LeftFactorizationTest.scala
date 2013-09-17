package org.jgll.grammar.leftfactorization

import org.jgll.grammar.Grammar
import org.jgll.grammar.GrammarBuilder
import org.jgll.grammar.RawGrammarBank
import org.junit.Test
//remove if not needed
import scala.collection.JavaConversions._

class LeftFactorizationTest {

  @Test
  def test() {
    val builder = RawGrammarBank.arithmeticExpressions()
    builder.leftFactorize("E")
    val grammar = builder.build()
    println(grammar)
  }
}
