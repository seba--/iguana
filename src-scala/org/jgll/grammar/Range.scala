package org.jgll.grammar

import java.util.BitSet
import org.jgll.grammar.condition.Condition
import org.jgll.parser.HashFunctions
import scala.reflect.{BeanProperty, BooleanBeanProperty}

import collection.mutable._
import scala.virtualization.lms.common.Base

trait RangeTrait {
  self: TerminalTrait
   with Base =>

  @SerialVersionUID(1L)
  class Range(@BeanProperty val start: Int, @BeanProperty val end: Int) extends AbstractSymbol with Terminal {

    private var testSet: BitSet = new BitSet()

    if (end < start) {
      throw new IllegalArgumentException("Start cannot be less than end.")
    }

    testSet.set(start, end + 1)

    override def `match`(i: Rep[Int]): Boolean = testSet.get(i)

    override def toString(): String = getName

    override def getMatchCode(): String = {
      "(I[ci] >= " + start + " && I[ci] <= " + end + ")"
    }

    override def hashCode(): Int = {
      HashFunctions.defaulFunction().hash(start, end)
    }

    override def equals(obj: Any): Boolean = {
      if (!(obj.isInstanceOf[Range])) {
        return false
      }
      val other = obj.asInstanceOf[Range]
      start == other.start && end == other.end
    }

    override def getName(): String = {
      "[" + start.toChar + "-" + end.toChar + "]"
    }

    override def asBitSet(): BitSet = testSet

    override def addConditions(conditions: ListBuffer[Condition]): Terminal = {
      val range = new Range(this.start, this.end)
      range.conditions.++=(this.conditions)
      range.conditions.++=(conditions)
      range
    }
  }
}