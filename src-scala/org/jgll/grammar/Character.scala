package org.jgll.grammar

import java.util.BitSet
import org.jgll.grammar.condition.Condition

import collection.mutable._

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class Character(private val c: Int) extends AbstractSymbol with Terminal {

  def get(): Int = c

  override def `match`(i: Int): Boolean = c == i

  override def toString(): String = getName

  override def getMatchCode(): String = "I[ci] == " + c

  override def hashCode(): Int = c

  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[Character])) {
      return false
    }
    val other = obj.asInstanceOf[Character]
    c == other.c
  }

  override def getName(): String = "[" + c.toChar + "]"

  override def asBitSet(): BitSet = {
    val set = new BitSet()
    set.set(c)
    set
  }

  override def addConditions(conditions: ListBuffer[Condition]): Terminal = {
    val terminal = new Character(this.c)
    terminal.conditions.++=(this.conditions)
    terminal.conditions.++=(conditions)
    terminal
  }
}
