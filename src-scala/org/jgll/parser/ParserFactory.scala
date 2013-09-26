package org.jgll.parser

import org.jgll.grammar.GrammarTrait
import org.jgll.lookup.{LevelBasedLookupTableTrait, RecursiveDescentLookupTableTrait}

trait ParserFactoryTrait {
  self: GrammarTrait
   with GLLParserTrait
   with LevelBasedLookupTableTrait =>
  trait ParserFactory extends GLLParserImplTrait with RecursiveDescentLookupTableTrait {

    def recursiveDescentParser(grammar: Grammar): GLLParser = {
      new GLLParserImpl(new RecursiveDescentLookupTable(grammar))
    }

    def levelParser(grammar: Grammar): GLLParser = {
      new GLLParserImpl(new LevelBasedLookupTable(grammar))
    }
  }
}