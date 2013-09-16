package org.jgll.parser

import org.jgll.grammar.Grammar
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.util.Input
//remove if not needed
import scala.collection.JavaConversions._

trait GLLParser {

  def parse(input: Input, grammar: Grammar, startSymbolName: String): NonterminalSymbolNode
}
