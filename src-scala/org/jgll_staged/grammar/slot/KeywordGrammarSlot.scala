package org.jgll_staged.grammar.slot

import java.io.IOException
import java.io.Writer
import org.jgll_staged.grammar.java.lang.Character
import org.jgll_staged.grammar.Keyword
import org.jgll_staged.grammar.Symbol
import org.jgll_staged.grammar.Terminal
import org.jgll_staged.grammar.slotaction.SlotAction
import org.jgll_staged.parser.GLLParserInternals
import org.jgll_staged.recognizer.GLLRecognizer
import org.jgll_staged.sppf.NonPackedNode
import org.jgll_staged.util.Input
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class KeywordGrammarSlot(position: Int, 
    protected var keywordHead: HeadGrammarSlot, 
    protected var keyword: Keyword, 
    previous: BodyGrammarSlot, 
    head: HeadGrammarSlot) extends BodyGrammarSlot(position, previous, head) {

  if (keywordHead == null) {
    throw new IllegalArgumentException("Keyword head cannot be null.")
  }

  def copy(keywordHead: HeadGrammarSlot, previous: BodyGrammarSlot, head: HeadGrammarSlot): KeywordGrammarSlot = {
    val slot = new KeywordGrammarSlot(this.position, keywordHead, this.keyword, previous, head)
    slot.preConditions = preConditions
    slot.popActions = popActions
    slot
  }

  override def testFirstSet(index: Int, input: Input): Boolean = input.`match`(index, keyword.getChars)

  override def testFollowSet(index: Int, input: Input): Boolean = false

  override def codeIfTestSetCheck(writer: Writer) {
  }

  override def getSymbol(): Symbol = keyword

  def getFirstTerminal(): Terminal = {
    new java.lang.Character(keyword.getChars()(0))
  }

  def getKeyword(): Keyword = keyword

  override def isNullable(): Boolean = false

  override def codeParser(writer: Writer) {
  }

  override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = {
    val ci = parser.getCurrentInputIndex
    if (input.`match`(ci, keyword.getChars)) {
      if (executePreConditions(parser, input)) {
        return null
      }
      val sppfNode = parser.getKeywordStub(keyword, keywordHead, ci)
      if (next.isInstanceOf[LastGrammarSlot]) {
        parser.getNonterminalNode(next.asInstanceOf[LastGrammarSlot], sppfNode)
        if (checkPopActions(parser, input)) {
          return null
        }
        parser.pop()
        return null
      } else {
        parser.getIntermediateNode(next, sppfNode)
        if (checkPopActions(parser, input)) {
          return null
        }
      }
    } else {
      parser.recordParseError(this)
      return null
    }
    next
  }

  private def checkPopActions(parser: GLLParserInternals, input: Input): Boolean = {
    next.popActions.find(_.execute(parser, input)).map(true)
      .getOrElse(false)
  }

  override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = {
    val ci = recognizer.getCi
    if (!input.`match`(ci, keyword.getChars)) {
      return null
    }
    recognizer.update(recognizer.getCu, ci + keyword.size)
    next
  }

  override def isNameEqual(slot: BodyGrammarSlot): Boolean = {
    if (this == slot) {
      return true
    }
    if (!(slot.isInstanceOf[KeywordGrammarSlot])) {
      return false
    }
    val other = slot.asInstanceOf[KeywordGrammarSlot]
    keyword == other.keyword
  }
}
