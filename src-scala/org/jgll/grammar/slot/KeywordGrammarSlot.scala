package org.jgll.grammar.slot

import java.io.Writer
import org.jgll.grammar._
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.recognizer.GLLRecognizerTrait
import org.jgll.util.InputTrait

trait KeywordGrammarSlotTrait {
    self: KeywordTrait
     with BodyGrammarSlotTrait
     with LastGrammarSlotTrait
     with HeadGrammarSlotTrait
     with GLLParserInternalsTrait
     with GrammarSlotTrait
     with TerminalTrait
     with GLLRecognizerTrait
     with InputTrait
     with CharacterTrait
  =>
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

    override def testFirstSet(index: Int, input: Input): Rep[Boolean] = input.`match`(index, keyword.chars)

    override def testFollowSet(index: Int, input: Input): Boolean = false

    override def codeIfTestSetCheck(writer: Writer) {
    }

    override def getSymbol(): Symbol = keyword

    def getFirstTerminal(): Terminal = {
      new Character(keyword.chars(0))
    }

    def getKeyword(): Keyword = keyword

    override def isNullable(): Boolean = false

    override def codeParser(writer: Writer) {
    }

    override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = {
      val ci = parser.getCurrentInputIndex
      if (input.`match`(ci, keyword.chars)) {
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
      next.popActions.find(_.execute(parser, input)).isDefined
    }

    override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = {
      val ci = recognizer.getCi
      if (!input.`match`(ci, keyword.chars)) {
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
}