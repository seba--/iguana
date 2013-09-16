package org.jgll.parser

import org.jgll.grammar.Grammar
import org.jgll.lookup.LevelBasedLookupTable
import org.jgll.lookup.RecursiveDescentLookupTable
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
