package org.jgll.parser

import org.jgll.grammar.Grammar
import org.jgll.lookup.{RecursiveDescentLookupTableTrait, LevelBasedLookupTable}
//remove if not needed
import scala.collection.JavaConversions._

trait ParserFactory extends GLLParserImplTrait with RecursiveDescentLookupTableTrait {

  def recursiveDescentParser(grammar: Grammar): GLLParser = {
    new GLLParserImpl(new RecursiveDescentLookupTable(grammar))
  }

  def levelParser(grammar: Grammar): GLLParser = {
    new GLLParserImpl(new LevelBasedLookupTable(grammar))
  }
}
