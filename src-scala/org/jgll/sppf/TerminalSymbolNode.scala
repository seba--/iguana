package org.jgll.sppf

import java.util.Collections
import org.jgll.grammar.slot.GrammarSlot
import org.jgll.parser.HashFunctions
import org.jgll.traversal.SPPFVisitor
import org.jgll.util.hashing.ExternalHasher
import org.jgll.util.hashing.hashfunction.HashFunction
import TerminalSymbolNode._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object TerminalSymbolNode {

  val externalHasher = new TerminalSymbolNodeExternalHasher()

  val EPSILON = -2

  @SerialVersionUID(1L)
  class TerminalSymbolNodeExternalHasher extends ExternalHasher[TerminalSymbolNode] {

    override def hash(t: TerminalSymbolNode, f: HashFunction): Int = f.hash(t.inputIndex, t.matchedChar)

    override def equals(t1: TerminalSymbolNode, t2: TerminalSymbolNode): Boolean = {
      t1.matchedChar == t2.matchedChar && t1.inputIndex == t2.inputIndex
    }
  }
}

class TerminalSymbolNode(@BeanProperty val matchedChar: Int, private val inputIndex: Int)
    extends SPPFNode {

  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[TerminalSymbolNode])) {
      return false
    }
    val other = obj.asInstanceOf[TerminalSymbolNode]
    matchedChar == other.matchedChar && inputIndex == other.inputIndex
  }

  override def hashCode(): Int = {
    externalHasher.hash(this, HashFunctions.defaulFunction())
  }

  override def getLabel(): String = {
    if (matchedChar == EPSILON) "ε" else matchedChar.toChar + ""
  }

  override def toString(): String = {
    "(%s, %d, %d)".format(getLabel, inputIndex, getRightExtent)
  }

  override def getLeftExtent(): Int = inputIndex

  override def getRightExtent(): Int = {
    if (matchedChar == EPSILON) inputIndex else inputIndex + 1
  }

  override def accept(visitAction: SPPFVisitor) {
    visitAction.visit(this)
  }

  override def getChildAt(index: Int): SPPFNode = null

  override def childrenCount(): Int = 0

  override def getChildren(): java.lang.Iterable[SPPFNode] = Collections.emptyList()

  override def isAmbiguous(): Boolean = false

  override def getGrammarSlot(): GrammarSlot = {
    throw new UnsupportedOperationException()
  }

  override def getLevel(): Int = inputIndex
}
