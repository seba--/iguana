package org.jgll.grammar

import java.io.Serializable
import org.jgll.grammar.slot.BodyGrammarSlot
import org.jgll.grammar.slot.EpsilonGrammarSlot
import org.jgll.grammar.slot.HeadGrammarSlot
import org.jgll.grammar.slot.KeywordGrammarSlot
import org.jgll.grammar.slot.LastGrammarSlot
import org.jgll.grammar.slot.NonterminalGrammarSlot
import org.jgll.grammar.slot.TerminalGrammarSlot
import org.jgll.util.hashing.HashFunctionBuilder
import org.jgll.util.hashing.hashfunction.MurmurHash3
import scala.reflect.{BeanProperty, BooleanBeanProperty}
import collection.mutable._

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class Alternate(@BeanProperty val firstSlot: BodyGrammarSlot) extends Serializable {

  private val symbols = ListBuffer[BodyGrammarSlot]()

  @BeanProperty
  var condition: BodyGrammarSlot = _

  if (firstSlot == null) {
    throw new IllegalArgumentException("firstSlot cannot be null.")
  }

  var current = firstSlot

  if (firstSlot.isInstanceOf[LastGrammarSlot]) {
    symbols += firstSlot
  }

  while (!(current.isInstanceOf[LastGrammarSlot])) {
    symbols += current
    current = current.next()
  }

  def getSymbolAt(index: Int): Symbol = symbols.get(index).getSymbol

  def isEmpty(): Boolean = {
    firstSlot.isInstanceOf[EpsilonGrammarSlot]
  }

  def isNullable(): Boolean = {
    if (isEmpty) return true
    var slot = firstSlot
    while (!(slot.isInstanceOf[LastGrammarSlot])) {
      if (slot.isInstanceOf[TerminalGrammarSlot] || slot.isInstanceOf[KeywordGrammarSlot]) return false
      if (slot.isInstanceOf[NonterminalGrammarSlot] && !slot.isNullable) return false
      slot = slot.next()
    }
    true
  }

  def getSlotAt(index: Int): BodyGrammarSlot = symbols.get(index)

  def getLastSlot(): BodyGrammarSlot = symbols.get(symbols.size - 1)

  def size(): Int = symbols.size

  def getNonterminalAt(index: Int): HeadGrammarSlot = {
    val bodyGrammarSlot = symbols.get(index)
    if (!(bodyGrammarSlot.isInstanceOf[NonterminalGrammarSlot])) {
      throw new RuntimeException("The symbol at " + index + " should be a nonterminal.")
    }
    bodyGrammarSlot.asInstanceOf[NonterminalGrammarSlot]
      .getNonterminal
  }

  def setNonterminalAt(index: Int, head: HeadGrammarSlot) {
    val bodyGrammarSlot = symbols.get(index)
    if (!(bodyGrammarSlot.isInstanceOf[NonterminalGrammarSlot])) {
      throw new RuntimeException("The symbol at " + index + " should be a nonterminal.")
    }
    bodyGrammarSlot.asInstanceOf[NonterminalGrammarSlot]
      .setNonterminal(head)
  }

  override def toString(): String = {
    val sb = new StringBuilder()
    for (s <- symbols) {
      sb.append(s.getSymbol).append(" ")
    }
    sb.toString
  }

  def isBinary(head: HeadGrammarSlot): Boolean = {
    if (!(symbols.get(0).isInstanceOf[NonterminalGrammarSlot] && 
      symbols.get(symbols.size - 1).isInstanceOf[NonterminalGrammarSlot])) {
      return false
    }
    val firstNonterminal = symbols.get(0).asInstanceOf[NonterminalGrammarSlot]
    val lastNonterminal = symbols.get(symbols.size - 1).asInstanceOf[NonterminalGrammarSlot]
    head.getNonterminal == firstNonterminal.getNonterminal.getNonterminal && 
      head.getNonterminal == lastNonterminal.getNonterminal.getNonterminal
  }

  def isUnaryPrefix(head: HeadGrammarSlot): Boolean = {
    if (isBinary(head)) {
      return false
    }
    val index = symbols.size - 1
    if (!(symbols.get(index).isInstanceOf[NonterminalGrammarSlot])) {
      return false
    }
    val firstNonterminal = symbols.get(index).asInstanceOf[NonterminalGrammarSlot]
    head.getNonterminal == firstNonterminal.getNonterminal.getNonterminal
  }

  def isUnaryPostfix(head: HeadGrammarSlot): Boolean = {
    if (isBinary(head)) {
      return false
    }
    val index = 0
    if (!(symbols.get(index).isInstanceOf[NonterminalGrammarSlot])) {
      return false
    }
    val lastNonterminal = symbols.get(index).asInstanceOf[NonterminalGrammarSlot]
    head.getNonterminal == lastNonterminal.getNonterminal.getNonterminal
  }

  def `match`(list: ListBuffer[Symbol]): Boolean = {
    if (list.size != symbols.size) {
      return false
    }
    for (i <- 0 until symbols.size if symbols.get(i).getSymbol != list.get(i)) {
      return false
    }
    true
  }

  def `match`(set: Set[ListBuffer[Symbol]]): Boolean = {
    set.find(`match`(_)).isDefined
  }

  override def hashCode(): Int = {
    val hashBuilder = new HashFunctionBuilder(new MurmurHash3())
    var current = firstSlot
    while (current != null) {
      if (current.isInstanceOf[NonterminalGrammarSlot]) {
        hashBuilder.addInt(current.asInstanceOf[NonterminalGrammarSlot].getNonterminal
          .getNonterminal
          .hashCode)
      } else if (current.isInstanceOf[TerminalGrammarSlot]) {
        hashBuilder.addInt(current.asInstanceOf[TerminalGrammarSlot].getTerminal
          .hashCode)
      } else if (current.isInstanceOf[KeywordGrammarSlot]) {
        hashBuilder.addInt(current.asInstanceOf[KeywordGrammarSlot].getKeyword
          .hashCode)
      } else {
        hashBuilder.addInt(0)
      }
      current = current.next()
    }
    hashBuilder.hash()
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (!(obj.isInstanceOf[Alternate])) {
      return false
    }
    val other = obj.asInstanceOf[Alternate]
    if (this.size != other.size) {
      return false
    }
    var thisSlot = firstSlot
    var otherSlot = other.firstSlot
    while (!(thisSlot.isInstanceOf[LastGrammarSlot])) {
      if (!thisSlot.isNameEqual(otherSlot)) {
        return false
      }
      thisSlot = thisSlot.next()
      otherSlot = otherSlot.next()
    }
    true
  }

  def getSymbols(): Iterable[Symbol] = {
    new Iterable[Symbol]() {

      override def iterator(): Iterator[Symbol] = {
        new Iterator[Symbol]() {

          private var current: BodyGrammarSlot = firstSlot

          override def hasNext(): Boolean = current.next() != null

          override def next(): Symbol = {
            val s = current.getSymbol
            current = current.next()
            s
          }

          def remove() {
            throw new UnsupportedOperationException()
          }
        }
      }
    }
  }
}
