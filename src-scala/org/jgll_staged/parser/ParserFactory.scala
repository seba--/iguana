package org.jgll_staged.parser

import org.jgll_staged.grammar.Grammar
import org.jgll_staged.lookup.LevelBasedLookupTable
import org.jgll_staged.lookup.RecursiveDescentLookupTable
//remove if not needed
import scala.collection.JavaConversions._

object ParserFactory {

  def recursiveDescentParser(grammar: Grammar): GLLParser = {
    new GLLParserImpl(new RecursiveDescentLookupTable(grammar))
  }

  def levelParser(grammar: Grammar): GLLParser = {
    new GLLParserImpl(new LevelBasedLookupTable(grammar))
  }
}
