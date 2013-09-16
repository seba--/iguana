package org.jgll_staged.grammar.slot

import java.io.IOException
import java.io.Writer
import java.util.BitSet
import org.jgll_staged.grammar.Symbol
import org.jgll_staged.grammar.Terminal
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.recognizer.GLLRecognizer
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class NonterminalGrammarSlot(position: Int, 
    previous: BodyGrammarSlot, 
    protected var nonterminal: HeadGrammarSlot, 
    head: HeadGrammarSlot) extends BodyGrammarSlot(position, previous, head) {

  private var firstSet: BitSet = new BitSet()

  private var followSet: BitSet = new BitSet()

  if (nonterminal == null) {
    throw new IllegalArgumentException("Nonterminal cannot be null.")
  }

  def copy(previous: BodyGrammarSlot, nonterminal: HeadGrammarSlot, head: HeadGrammarSlot): NonterminalGrammarSlot = {
    val slot = new NonterminalGrammarSlot(this.position, previous, nonterminal, head)
    slot.preConditions = preConditions
    slot.popActions = popActions
    slot.firstSet = firstSet
    slot.followSet = followSet
    slot
  }

  def getNonterminal(): HeadGrammarSlot = nonterminal

  def setNonterminal(nonterminal: HeadGrammarSlot) {
    this.nonterminal = nonterminal
  }

  override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = {
    val ci = parser.getCurrentInputIndex
    if (!testFirstSet(ci, input) && !(isNullable && testFollowSet(ci, input))) {
      parser.recordParseError(this)
      return null
    }
    if (executePreConditions(parser, input)) {
      return null
    }
    parser.createGSSNode(next)
    nonterminal
  }

  override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = {
    val ci = recognizer.getCi
    val cu = recognizer.getCu
    if (testFirstSet(ci, input)) {
      recognizer.update(recognizer.create(next, cu, ci), ci)
      nonterminal
    } else if (isNullable && testFollowSet(ci, input)) {
      next
    } else {
      recognizer.recognitionError(cu, ci)
      null
    }
  }

  override def codeParser(writer: Writer) {
    if (previous == null) {
      codeIfTestSetCheck(writer)
      writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n")
      writer.append("   label = " + nonterminal.getId + ";\n")
      codeElseTestSetCheck(writer)
      writer.append("}\n")
      writer.append("// " + next + "\n")
      writer.append("private void parse_" + next.id + "() {\n")
      var slot = next
      while (slot != null) {
        slot.codeParser(writer)
        slot = slot.next
      }
    } else {
      codeIfTestSetCheck(writer)
      writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n")
      writer.append("   label = " + nonterminal.getId + ";\n")
      codeElseTestSetCheck(writer)
      writer.append("}\n")
      writer.append("// " + next + "\n")
      writer.append("private void parse_" + next.id + "(){\n")
    }
  }

  override def codeIfTestSetCheck(writer: Writer) {
    writer.append("if (")
    writer.append(") {\n")
  }

  def setTestSet() {
    for (t <- nonterminal.getFirstSet) {
      firstSet.or(t.asBitSet())
    }
    for (t <- nonterminal.getFollowSet) {
      followSet.or(t.asBitSet())
    }
  }

  override def testFirstSet(index: Int, input: Input): Boolean = firstSet.get(input.charAt(index))

  override def testFollowSet(index: Int, input: Input): Boolean = followSet.get(input.charAt(index))

  override def isNullable(): Boolean = nonterminal.isNullable

  override def getSymbol(): Symbol = nonterminal.getNonterminal

  override def isNameEqual(slot: BodyGrammarSlot): Boolean = {
    if (this == slot) {
      return true
    }
    if (!(slot.isInstanceOf[NonterminalGrammarSlot])) {
      return false
    }
    val other = slot.asInstanceOf[NonterminalGrammarSlot]
    nonterminal.getNonterminal == other.nonterminal.getNonterminal
  }
}
