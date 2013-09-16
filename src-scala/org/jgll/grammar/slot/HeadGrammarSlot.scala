package org.jgll.grammar.slot

import java.io.IOException
import java.io.Writer
import java.util.ArrayList
import java.util.HashSet
import java.util.Iterator
import java.util.List
import java.util.Set
import org.jgll.grammar.Alternate
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Symbol
import org.jgll.grammar.Terminal
import org.jgll.parser.GLLParserInternals
import org.jgll.recognizer.GLLRecognizer
import org.jgll.util.Input
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class HeadGrammarSlot(@BeanProperty val nonterminal: Nonterminal) extends GrammarSlot {

  private var alternates: List[Alternate] = new ArrayList()

  @BooleanBeanProperty
  var nullable: Boolean = _

  @BooleanBeanProperty
  var directNullable: Boolean = _

  @BeanProperty
  val firstSet = new HashSet[Terminal]()

  @BeanProperty
  val followSet = new HashSet[Terminal]()

  @BeanProperty
  var epsilonAlternate: Alternate = _

  def addAlternate(alternate: Alternate) {
    alternates.add(alternate)
  }

  def setAlternates(alternates: List[Alternate]) {
    this.alternates = alternates
  }

  def removeAlternate(alternate: Alternate) {
    alternates.remove(alternate)
  }

  def without(list: List[Symbol]): Set[Alternate] = {
    val set = new HashSet[Alternate](alternates)
    for (alternate <- alternates if alternate.`match`(list)) {
      set.remove(alternate)
    }
    set
  }

  def without(withoutSet: Set[List[Symbol]]): Set[Alternate] = {
    val set = new HashSet[Alternate](alternates)
    for (alternate <- alternates; list <- withoutSet if alternate.`match`(list)) {
      set.remove(alternate)
    }
    set
  }

  def remove(list: List[Symbol]) {
    val it = alternates.iterator()
    while (it.hasNext) {
      val alternate = it.next()
      if (alternate.`match`(list)) {
        it.remove()
      }
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
      writer.append("   add(grammar.getGrammarSlot(" + alternate.getFirstSlot.getId + 
        "), cu, ci, DummyNode.getInstance());\n")
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

  def getAlternateAt(index: Int): Alternate = alternates.get(index)

  def getAlternates(): List[Alternate] = new ArrayList(alternates)

  def getAlternatesAsSet(): Set[Alternate] = new HashSet(alternates)

  def getCountAlternates(): Int = alternates.size

  def contains(list: List[Symbol]): Boolean = {
    alternates.find(_.`match`(list)).isDefined
  }

  def contains(set: Set[List[Symbol]]): Boolean = {
    set.find(contains(_)).isDefined
  }

  override def toString(): String = nonterminal.toString
}
