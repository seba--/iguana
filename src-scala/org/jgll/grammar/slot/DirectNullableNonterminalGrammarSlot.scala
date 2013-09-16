package org.jgll.grammar.slot

import org.jgll.parser.GLLParserInternals
import org.jgll.recognizer.GLLRecognizer
import org.jgll.sppf.NonPackedNode
import org.jgll.util.Input
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class DirectNullableNonterminalGrammarSlot(position: Int, 
    previous: BodyGrammarSlot, 
    nonterminal: HeadGrammarSlot, 
    head: HeadGrammarSlot) extends NonterminalGrammarSlot(position, previous, nonterminal, head) {

  override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = {
    val ci = parser.getCurrentInputIndex
    if (executePreConditions(parser, input)) {
      return null
    }
    if (testFirstSet(ci, input)) {
      parser.createGSSNode(next)
      nonterminal
    } else if (testFollowSet(ci, input)) {
      val node = parser.getLookupTable.getNonPackedNode(nonterminal, ci, ci)
      node.addFirstPackedNode(nonterminal.getEpsilonAlternate.getFirstSlot, ci)
      if (next.isInstanceOf[LastGrammarSlot]) {
        parser.getNonterminalNode(next.asInstanceOf[LastGrammarSlot], node)
        parser.pop()
        return null
      } else {
        parser.getIntermediateNode(next, node)
      }
      next
    } else {
      parser.recordParseError(this)
      null
    }
  }

  override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = super.recognize(recognizer, input)
}
