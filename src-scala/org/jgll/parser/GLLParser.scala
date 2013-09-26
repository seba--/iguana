package org.jgll.parser

import org.jgll.grammar.GrammarTrait
import org.jgll.sppf.NonterminalSymbolNodeTrait
import org.jgll.util.InputTrait

trait GLLParserTrait { self: InputTrait with GrammarTrait with NonterminalSymbolNodeTrait =>
  trait GLLParser {

    def parse(input: Input, grammar: Grammar, startSymbolName: String): NonterminalSymbolNode
  }
}