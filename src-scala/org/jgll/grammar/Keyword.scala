package org.jgll.grammar

import org.jgll.grammar.condition.Condition
import org.jgll.parser.HashFunctions
import org.jgll.util.hashing.ExternalHasher
import org.jgll.util.hashing.hashfunction.HashFunction
import scala.reflect.{BeanProperty, BooleanBeanProperty}

import collection.mutable._
import org.jgll.util.InputTrait

trait KeywordTrait { self: InputTrait with TerminalTrait with CharacterTrait =>
  import Keyword._
  object Keyword {

    val externalHasher = new KeywordExternalHasher()

    @SerialVersionUID(1L)
    class KeywordExternalHasher extends ExternalHasher[Keyword] {

      override def hash(k: Keyword, f: HashFunction): Int = f.hash(k.chars)

      override def equals(k1: Keyword, k2: Keyword): Boolean = k1.chars == k2.chars
    }
  }

  @SerialVersionUID(1L)
  class Keyword(@BeanProperty val name: String, val chars: Array[Int]) extends AbstractSymbol {

    def this(name: String, s: String) {
      this(name, Input.toIntArray(s))
    }

    def this(name: String, chars: Array[Char]) {
      this(name, chars map (_.toInt))
    }

    def size(): Int = chars.length

    def getFirstTerminal(): Terminal = new Character(chars(0))

    override def toString(): String = getName

    override def equals(obj: Any): Boolean = {
      if (!(obj.isInstanceOf[Keyword])) {
        return false
      }
      val other = obj.asInstanceOf[Keyword]
      chars == other.chars
    }

    override def hashCode(): Int = {
      externalHasher.hash(this, HashFunctions.defaulFunction())
    }

    override def addConditions(conditions: ListBuffer[Condition]): Keyword = {
      val keyword = new Keyword(this.name, this.chars)
      keyword.conditions.++=(this.conditions)
      keyword.conditions.++=(conditions)
      keyword
    }
  }
}