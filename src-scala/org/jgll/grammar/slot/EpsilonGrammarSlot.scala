package org.jgll.grammar.slot

import java.io.IOException
import java.io.Writer
import org.jgll.parser.GLLParserInternals
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.util.Input
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class EpsilonGrammarSlot(position: Int, head: HeadGrammarSlot, `object`: AnyRef)
    extends LastGrammarSlot(position, null, head, `object`) {

  override def parse(parser: GLLParserInternals, intput: Input): GrammarSlot = {
    val cr = parser.getEpsilonNode
    parser.getNonterminalNode(this, cr)
    parser.pop()
    null
  }

  override def codeParser(writer: Writer) {
    writer.append("   cr = getNodeT(-2, ci);\n")
    writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + id + "), cn, cr);\n")
    writer.append("   pop(cu, ci, cn);\n")
    writer.append("   label = L0;\n}\n")
  }

  override def testFirstSet(index: Int, input: Input): Boolean = true
}
