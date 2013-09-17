package org.jgll.grammar.patterns

import java.io.Serializable
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Symbol
import org.jgll.parser.HashFunctions

import collection.mutable._

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class AbstractPattern(nonteriminal: Nonterminal, 
    _parent: ListBuffer[Symbol],
    protected val position: Int, 
    _child: ListBuffer[Symbol]) extends Serializable {

  protected val parent = ListBuffer[Symbol]() ++ _parent

  protected val child = ListBuffer[Symbol]() ++ _child

  protected val nonterminal = nonteriminal

  if (parent == null || child == null) {
    throw new IllegalArgumentException("parent or child alternates cannot be null.")
  }

  def getPosition(): Int = position

  def getParent(): ListBuffer[Symbol] = parent

  def getChild(): ListBuffer[Symbol] = child

  def getNonterminal(): Nonterminal = nonterminal

  def getFilteredNontemrinalName(): String = parent.get(position).getName

  override def hashCode(): Int = {
    HashFunctions.defaulFunction().hash(nonterminal.hashCode, position, parent.hashCode)
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (!(obj.isInstanceOf[AbstractPattern])) {
      return false
    }
    val other = obj.asInstanceOf[AbstractPattern]
    other.nonterminal == this.nonterminal && other.position == this.position && 
      other.parent == this.parent
  }

  override def toString(): String = {
    val sb = new StringBuilder()
    sb.append("(")
    sb.append(nonterminal)
    sb.append(", ")
    var i = 0
    for (symbol <- parent) {
      if (i == position) {
        sb.append(". ")
      }
      sb.append(symbol).append(" ")
      i += 1
    }
    sb.delete(sb.length - 1, sb.length)
    sb.append(", ")
    i = 0
    for (symbol <- child) {
      sb.append(symbol).append(" ")
    }
    sb.delete(sb.length - 1, sb.length)
    sb.append(")")
    sb.toString
  }
}
