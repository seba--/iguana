package org.jgll.grammar.slot

import java.io.Writer
import org.jgll.grammar._
import org.jgll.parser.GLLParserInternalsTrait
import org.jgll.recognizer.GLLRecognizerTrait
import scala.reflect.{BeanProperty, BooleanBeanProperty}

import scala.collection.mutable._
import org.jgll.util.InputTrait

trait HeadGrammarSlotTrait {
  self: GrammarSlotTrait
   with AlternateTrait
   with GLLParserInternalsTrait
   with InputTrait
   with TerminalTrait
   with GLLRecognizerTrait
   =>
  @SerialVersionUID(1L)
  class HeadGrammarSlot(@BeanProperty val nonterminal: Nonterminal) extends GrammarSlot {

    private var alternates: ListBuffer[Alternate] = ListBuffer[Alternate]()

    @BooleanBeanProperty
    var nullable: Boolean = _

    @BooleanBeanProperty
    var directNullable: Boolean = _

    @BeanProperty
    val firstSet = HashSet[Terminal]()

    @BeanProperty
    val followSet = HashSet[Terminal]()

    @BeanProperty
    var epsilonAlternate: Alternate = _

    def addAlternate(alternate: Alternate) {
      alternates += (alternate)
    }

    def setAlternates(alternates: ListBuffer[Alternate]) {
      this.alternates = alternates
    }

    def removeAlternate(alternate: Alternate) {
      alternates -= (alternate)
    }

    def without(list: ListBuffer[Symbol]): Set[Alternate] = {
      val set = new HashSet[Alternate]() ++= alternates
      for (alternate <- alternates if alternate.`match`(list)) {
        set.remove(alternate)
      }
      set
    }

    def without(withoutSet: Set[ListBuffer[Symbol]]): Set[Alternate] = {
      val set = new HashSet[Alternate]() ++= alternates
      for (alternate <- alternates; list <- withoutSet if alternate.`match`(list)) {
        set.remove(alternate)
      }
      set
    }

    def remove(list: ListBuffer[Symbol]) {
      alternates foreach { alternate =>
        if (alternate.`match`(list))
          alternates -= alternate
      }
    }

    def removeAllAlternates() {
      alternates.clear()
    }

    def setNullable(nullable: Boolean, directNullable: Boolean) {
      this.nullable = nullable
      this.directNullable = directNullable
    }

    override def parse(parser: GLLParserInternals, input: Input): GrammarSlot = {
      for (alternate <- alternates) {
        val ci = parser.getCurrentInputIndex
        val slot = alternate.getFirstSlot
        if (slot.testFirstSet(ci, input) || (slot.isNullable && slot.testFollowSet(ci, input))) {
          parser.addDescriptor(slot)
        }
      }
      null
    }

    override def recognize(recognizer: GLLRecognizer, input: Input): GrammarSlot = {
      for (alternate <- alternates) {
        val ci = recognizer.getCi
        val slot = alternate.getFirstSlot
        if (slot.testFirstSet(ci, input) || (slot.isNullable && slot.testFollowSet(ci, input))) {
          val cu = recognizer.getCu
          recognizer.add(alternate.getFirstSlot, cu, ci)
        }
      }
      null
    }

    override def codeParser(writer: Writer) {
      writer.append("// " + nonterminal.getName + "\n")
      writer.append("private void parse_" + id + "() {\n")
      for (alternate <- alternates) {
        writer.append("   //" + alternate.getFirstSlot + "\n")
        alternate.getFirstSlot.codeIfTestSetCheck(writer)
        writer.append("   add(grammar.getGrammarSlot(" + alternate.getFirstSlot.getId + "), cu, ci, DummyNode());\n")
        writer.append("}\n")
      }
      writer.append("   label = L0;\n")
      writer.append("}\n")
      for (alternate <- alternates) {
        writer.append("// " + alternate + "\n")
        writer.append("private void parse_" + alternate.getFirstSlot.getId +
          "() {\n")
        alternate.getFirstSlot.codeParser(writer)
      }
    }

    def getAlternateAt(index: Int): Alternate = alternates(index)

    def getAlternates(): ListBuffer[Alternate] = ListBuffer[Alternate]() ++= alternates

    def getAlternatesAsSet(): Set[Alternate] = new HashSet() ++= alternates

    def getCountAlternates(): Int = alternates.size

    def contains(list: ListBuffer[Symbol]): Boolean = {
      alternates.find(_.`match`(list)).isDefined
    }

    def contains(set: Set[ListBuffer[Symbol]]): Boolean = {
      set.find(contains(_)).isDefined
    }

    override def toString(): String = nonterminal.toString
  }
}