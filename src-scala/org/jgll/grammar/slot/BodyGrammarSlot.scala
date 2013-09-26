package org.jgll.grammar.slot

import java.io.Serializable
import java.io.Writer
import org.jgll.grammar.Symbol
import org.jgll.grammar.slotaction.SlotActionTrait
import org.jgll.parser.GLLParserInternalsTrait
import scala.collection.mutable.ListBuffer
import org.jgll.util.InputTrait


trait BodyGrammarSlotTrait {
  self: GrammarSlotTrait with InputTrait with HeadGrammarSlotTrait with SlotActionTrait with GLLParserInternalsTrait =>
  @SerialVersionUID(1L)
  abstract class BodyGrammarSlot(protected val position: Int, protected var _previous: BodyGrammarSlot, protected var head: HeadGrammarSlot)
      extends GrammarSlot with Serializable {

    protected var _next: BodyGrammarSlot = _

    protected var preConditions: ListBuffer[SlotAction[Boolean]] = ListBuffer()

    var popActions: ListBuffer[SlotAction[Boolean]] = ListBuffer()

    private var label: String = _

    if (position < 0) {
      throw new IllegalArgumentException("Position cannot be negative.")
    }

    if (_previous != null) {
      _previous._next = this
    }

    def addPopAction(popAction: SlotAction[Boolean]) {
      popActions += (popAction)
    }

    def getPopActions(): ListBuffer[SlotAction[Boolean]] = popActions

    def addPreCondition(preCondition: SlotAction[Boolean]) {
      preConditions += (preCondition)
    }

    def getPreConditions(): ListBuffer[SlotAction[Boolean]] = preConditions

    protected def executePreConditions(parser: GLLParserInternals, input: Input): Boolean = {
      preConditions.find(_.execute(parser, input)).isDefined
    }

    def testFirstSet(index: Int, input: Input): Boolean

    def testFollowSet(index: Int, input: Input): Boolean

    def codeIfTestSetCheck(writer: Writer): Unit

    def isFirst(): Boolean = _previous == null

    def codeElseTestSetCheck(writer: Writer) {
      writer.append("} else { newParseError(grammar.getGrammarSlot(" + this.id +
        "), ci); label = L0; return; } \n")
    }

    def next(): BodyGrammarSlot = _next

    def previous(): BodyGrammarSlot = _previous

    def setPrevious(previous: BodyGrammarSlot) {
      this._previous = previous
    }

    def setNext(next: BodyGrammarSlot) {
      this._next = next
    }

    def getPosition(): Int = position

    def getHead(): HeadGrammarSlot = head

    def getSymbol(): Symbol

    def isNullable(): Boolean

    def isNameEqual(slot: BodyGrammarSlot): Boolean

    def setLabel(label: String) {
      this.label = label
    }

    override def toString(): String = label
  }
}