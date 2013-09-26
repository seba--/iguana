package org.jgll.grammar

import java.util.BitSet
import org.jgll.grammar.condition.Condition
import scala.reflect.BeanProperty
import collection.mutable._
import scala.virtualization.lms.common.Base

trait CharacterClassTrait {
  self: TerminalTrait
   with RangeTrait
   with Base =>
  @SerialVersionUID(1L)
  class CharacterClass(@BeanProperty val ranges: ListBuffer[Range]) extends AbstractSymbol with Terminal {

    private var testSet: BitSet = new BitSet()

    if (ranges == null || ranges.size == 0) {
      throw new IllegalArgumentException("Ranges cannot be null or empty.")
    }

    for (range <- ranges) {
      testSet.or(range.asBitSet())
    }

    override def `match`(i: Rep[Int]): Boolean = testSet.get(i)

    override def toString(): String = getName

    override def getMatchCode(): String = {
      val sb = new StringBuilder()
      for (range <- ranges) {
        sb.append(range.getMatchCode).append(" || ")
      }
      sb.toString
    }

    override def hashCode(): Int = ranges.hashCode

    override def equals(obj: Any): Boolean = {
      if (!(obj.isInstanceOf[CharacterClass])) {
        return false
      }
      val other = obj.asInstanceOf[CharacterClass]
      testSet == other.testSet
    }

    override def getName(): String = {
      val sb = new StringBuilder()
      sb.append("[")
      for (range <- ranges) {
        sb.append(getChar(range.getStart)).append("-").append(getChar(range.getEnd))
      }
      sb.append("]")
      sb.toString
    }

    private def getChar(`val`: Int): String = {
      val c = `val`.toChar
      if (c == '-' || c == ' ') {
        return "\\" + c
      }
      if (c == '\r') {
        return "\\r"
      }
      if (c == '\n') {
        return "\\n"
      }
      c + ""
    }

    override def asBitSet(): BitSet = testSet

    override def addConditions(conditions: ListBuffer[Condition]): Terminal = {
      val characterClass = new CharacterClass(this.ranges)
      characterClass.conditions.++=(this.conditions)
      characterClass.conditions.++=(conditions)
      characterClass
    }
  }
}