package org.jgll_staged.parser

import org.jgll_staged.grammar.Grammar
import org.jgll_staged.sppf.NonterminalSymbolNode
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

trait GLLParser {

  def parse(input: Input, grammar: Grammar, startSymbolName: String): NonterminalSymbolNode
}
