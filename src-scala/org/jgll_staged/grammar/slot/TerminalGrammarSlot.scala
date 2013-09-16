package org.jgll_staged.grammar.slot

import java.io.IOException
import java.io.Writer
import org.jgll_staged.grammar.java.lang.Character
import org.jgll_staged.grammar.Range
import org.jgll_staged.grammar.Symbol
import org.jgll_staged.grammar.Terminal
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.recognizer.GLLRecognizer
import org.jgll_staged.sppf.TerminalSymbolNode
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class TerminalGrammarSlot(position: Int, 
    previous: BodyGrammarSlot, 
    protected val terminal: Terminal, 
    head: HeadGrammarSlot) extends BodyGrammarSlot(position, previous, head) {

  def copy(previous: BodyGrammarSlot, head: HeadGrammarSlot): TerminalGrammarSlot = {
    val slot = new TerminalGrammarSlot(this.position, previous, this.terminal, head)
    slot.preConditions = preConditions
    slot.popActions = popActions
    slot
  }

  override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = {
    val ci = parser.getCurrentInputIndex
    val charAtCi = input.charAt(ci)
    if (terminal.`match`(charAtCi)) {
      if (executePreConditions(parser, input)) {
        return null
      }
      val cr = parser.getTerminalNode(charAtCi)
      if (next.isInstanceOf[LastGrammarSlot]) {
        parser.getNonterminalNode(next.asInstanceOf[LastGrammarSlot], cr)
        parser.pop()
        return null
      } else {
        parser.getIntermediateNode(next, cr)
      }
    } else {
      parser.recordParseError(this)
      return null
    }
    next
  }

  override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = {
    var ci = recognizer.getCi
    val cu = recognizer.getCu
    val charAtCi = input.charAt(ci)
    if (previous == null && next.next() == null) {
      if (terminal.`match`(charAtCi)) {
        ci += 1
        recognizer.update(cu, ci)
      } else {
        recognizer.recognitionError(cu, ci)
        return null
      }
    } else if (previous == null && !(next.next() == null)) {
      if (terminal.`match`(charAtCi)) {
        ci += 1
        recognizer.update(cu, ci)
      } else {
        recognizer.recognitionError(cu, ci)
        return null
      }
    } else {
      if (terminal.`match`(charAtCi)) {
        ci += 1
        recognizer.update(cu, ci)
      } else {
        recognizer.recognitionError(cu, ci)
        return null
      }
    }
    next
  }

  override def codeParser(writer: Writer) {
    if (previous == null && next.next() == null) {
      writer.append(checkInput(terminal))
      writer.append("   cr = getNodeT(I[ci], ci);\n")
      codeElseTestSetCheck(writer)
      writer.append("   ci = ci + 1;\n")
      writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + next.getId + 
        "), cn, cr);\n")
      writer.append("   pop(cu, ci, cn);\n")
      writer.append("   label = L0;\n}\n")
    } else if (previous == null && !(next.next() == null)) {
      writer.append(checkInput(terminal))
      writer.append("   cn = getNodeT(I[ci], ci);\n")
      codeElseTestSetCheck(writer)
      writer.append("   ci = ci + 1;\n")
      var slot = next
      while (slot != null) {
        slot.codeParser(writer)
        slot = slot.next()
      }
    } else {
      writer.append(checkInput(terminal))
      writer.append("     cr = getNodeT(I[ci], ci);\n")
      codeElseTestSetCheck(writer)
      writer.append("   ci = ci + 1;\n")
      writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + next.getId + 
        "), cn, cr);\n")
    }
  }

  private def checkInput(terminal: Terminal): String = {
    var s = ""
    if (terminal.isInstanceOf[Range]) {
      s += "   if(I[ci] >= " + terminal.asInstanceOf[Range].getStart + 
        " + && I[ci] <= " + 
        terminal.asInstanceOf[Range].getEnd + 
        ") {\n"
    } else {
      s += "   if(I[ci] == " + terminal.asInstanceOf[java.lang.Character].get + 
        ") {\n"
    }
    s
  }

  override def testFirstSet(index: Int, input: Input): Boolean = terminal.`match`(input.charAt(index))

  override def testFollowSet(index: Int, input: Input): Boolean = false

  override def codeIfTestSetCheck(writer: Writer) {
    writer.append("if (").append(terminal.getMatchCode)
      .append(") {\n")
  }

  def getTerminal(): Terminal = terminal

  override def isNullable(): Boolean = false

  override def getSymbol(): Symbol = terminal

  override def isNameEqual(slot: BodyGrammarSlot): Boolean = {
    if (this == slot) {
      return true
    }
    if (!(slot.isInstanceOf[TerminalGrammarSlot])) {
      return false
    }
    val other = slot.asInstanceOf[TerminalGrammarSlot]
    terminal == other.terminal
  }
}
