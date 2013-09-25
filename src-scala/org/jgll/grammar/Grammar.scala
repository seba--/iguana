package org.jgll.grammar

import java.io.IOException
import java.io.Serializable
import java.io.Writer
import org.jgll.grammar.slot.BodyGrammarSlot
import org.jgll.grammar.slot.HeadGrammarSlot
import org.jgll.grammar.slot.KeywordGrammarSlot
import org.jgll.grammar.slot.L0
import org.jgll.grammar.slot.LastGrammarSlot
import org.jgll.grammar.slot.NonterminalGrammarSlot
import org.jgll.grammar.slot.TerminalGrammarSlot
import org.jgll.util.logging.LoggerWrapper
import scala.reflect.{BeanProperty, BooleanBeanProperty}

import collection.mutable._
import org.jgll.util.InputTrait

//remove if not needed
import scala.collection.JavaConversions._


trait GrammarTrait extends InputTrait {
  object Grammar {

    private val log = LoggerWrapper.getLogger(classOf[Grammar])
  }
  import Grammar._

  @SerialVersionUID(1L)
  class Grammar(builder: GrammarBuilder) extends Serializable {

    @BeanProperty
    var nonterminals: ListBuffer[HeadGrammarSlot] = builder.nonterminals

    private var slots: ListBuffer[BodyGrammarSlot] = builder.slots

    var nameToNonterminals: Map[Nonterminal, HeadGrammarSlot] = builder.nonterminalsMap

    private var nameToSlots: Map[String, BodyGrammarSlot] = new HashMap()

    private var newNonterminalsMap: Map[Nonterminal, ListBuffer[HeadGrammarSlot]] = builder.newNonterminalsMap

    private var newNonterminals: Set[HeadGrammarSlot] = new HashSet()

    @BeanProperty
    var name: String = builder.name

    @BeanProperty
    var longestTerminalChain: Int = builder.longestTerminalChain

    @BeanProperty
    var maximumNumAlternates: Int = builder.maximumNumAlternates

    @BeanProperty
    var maxDescriptorsAtInput: Int = builder.maxDescriptors

    @BeanProperty
    var averageDescriptorsAtInput: Int = builder.averageDescriptors

    @BeanProperty
    var stDevDescriptors: Int = builder.stDevDescriptors.toInt

    for (newNonterminals <- builder.newNonterminalsMap.values) {
      this.newNonterminals.addAll(newNonterminals)
    }

    for (slot <- slots) {
      nameToSlots.put(grammarSlotToString(slot), slot)
    }

    for (slot <- slots) {
      slot.setLabel(grammarSlotToString(slot))
    }

    printGrammarStatistics()

    def printGrammarStatistics() {
      log.info("Grammar information:")
      log.info("Nonterminals: %d", nonterminals.size)
      log.info("Production rules: %d", numProductions())
      log.info("Grammar slots: %d", slots.size)
      log.debug("Longest terminal Chain: %d", longestTerminalChain)
      log.debug("Maximum number alternates: %d", maximumNumAlternates)
      log.debug("Maximum descriptors: %d", maxDescriptorsAtInput)
      log.trace("Average descriptors: %d", averageDescriptorsAtInput)
      log.trace("Standard Deviation descriptors: %d", stDevDescriptors)
    }

    private def numProductions(): Int = {
      var num = 0
      for (head <- nonterminals) {
        num += head.getCountAlternates
      }
      num
    }

    def code(writer: Writer, packageName: String) {
      var header = Input.read(this.getClass.getResourceAsStream("ParserTemplate"))
      header = header.replace("${className}", name).replace("${packageName}", packageName)
      writer.append(header)
      writer.append("case " + L0.getInstance.getId + ":\n")
      L0.getInstance.codeParser(writer)
      for (nonterminal <- nonterminals) {
        writer.append("// " + nonterminal + "\n")
        writer.append("case " + nonterminal.getId + ":\n")
        writer.append("parse_" + nonterminal.getId + "();\n")
        writer.append("break;\n")
      }
      for (slot <- slots if !(slot.previous().isInstanceOf[TerminalGrammarSlot])) {
        writer.append("// " + slot + "\n")
        writer.append("case " + slot.getId + ":\n")
        writer.append("parse_" + slot.getId + "();\n")
        writer.append("break;\n")
      }
      writer.append("} } }\n")
      for (nonterminal <- nonterminals) {
        nonterminal.codeParser(writer)
      }
      writer.append("}")
    }

    def getNonterminal(id: Int): HeadGrammarSlot = nonterminals.get(id)

    def getGrammarSlot(id: Int): BodyGrammarSlot = slots.get(id)

    def getGrammarSlots(): ListBuffer[BodyGrammarSlot] = slots

    def getNonterminalByName(name: String): HeadGrammarSlot = {
      nameToNonterminals.getOrElse(new Nonterminal(name), null)
    }

    def getNonterminalByNameAndIndex(name: String, index: Int): HeadGrammarSlot = {
      newNonterminalsMap.get(new Nonterminal(name)).get(index - 1)
    }

    def isNewNonterminal(head: HeadGrammarSlot): Boolean = newNonterminals.contains(head)

    def getIndex(head: HeadGrammarSlot): Int = {
      val list = newNonterminalsMap.getOrElse(head.getNonterminal, return -1)
      list.indexOf(head) + 1
    }

    private def grammarSlotToString(slot: BodyGrammarSlot): String = {
      val sb = new StringBuilder()
      var current = slot
      sb.append(" . ")
      sb.append(getSlotName(current)).append(" ")
      current = slot.previous()
      while (current != null) {
        sb.insert(0, " " + getSlotName(current))
        current = current.previous()
      }
      current = slot.next()
      while (current != null) {
        sb.append(getSlotName(current)).append(" ")
        current = current.next()
      }
      sb.delete(sb.length - 2, sb.length)
      sb.insert(0, " ::=")
      sb.insert(0, getNonterminalName(slot.getHead))
      sb.toString
    }

    private def getSlotName(slot: BodyGrammarSlot): String = {
      if (slot.isInstanceOf[TerminalGrammarSlot]) {
        slot.asInstanceOf[TerminalGrammarSlot].getTerminal.getName
      } else if (slot.isInstanceOf[NonterminalGrammarSlot]) {
        getNonterminalName(slot.asInstanceOf[NonterminalGrammarSlot].getNonterminal)
      } else if (slot.isInstanceOf[KeywordGrammarSlot]) {
        slot.asInstanceOf[KeywordGrammarSlot].getKeyword.getName
      } else {
        ""
      }
    }

    def getGrammarSlotByName(name: String): BodyGrammarSlot = nameToSlots.getOrElse(name, null)

    override def toString(): String = {
      val sb = new StringBuilder()
      val action = new GrammarVisitAction() {

        override def visit(slot: LastGrammarSlot) {
          sb.append("\n")
        }

        override def visit(slot: TerminalGrammarSlot) {
          sb.append(" ").append(getSlotName(slot))
        }

        override def visit(slot: NonterminalGrammarSlot) {
          sb.append(" ").append(getSlotName(slot))
        }

        override def visit(head: HeadGrammarSlot) {
          sb.append(getNonterminalName(head)).append(" ::= ")
        }

        override def visit(slot: KeywordGrammarSlot) {
          sb.append(" ").append(getSlotName(slot))
        }
      }
      GrammarVisitor.visit(this, action)
      sb.toString
    }

    def getNonterminalName(head: HeadGrammarSlot): String = {
      val name = head.getNonterminal.getName
      if (newNonterminals.contains(head))
        name + (newNonterminalsMap.get(head.getNonterminal).get.indexOf(head) + 1)
      else name
    }
  }
}