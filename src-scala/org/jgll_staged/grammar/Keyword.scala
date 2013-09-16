package org.jgll_staged.grammar

import java.util.Arrays
import java.util.Collection
import org.jgll_staged.grammar.condition.Condition
import org.jgll_staged.parser.HashFunctions
import org.jgll_staged.util.Input
import org.jgll_staged.util.hashing.ExternalHasher
import org.jgll_staged.util.hashing.hashfunction.HashFunction
import Keyword._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object Keyword {

  val externalHasher = new KeywordExternalHasher()

  @SerialVersionUID(1L)
  class KeywordExternalHasher extends ExternalHasher[Keyword] {

    override def hash(k: Keyword, f: HashFunction): Int = f.hash(k.getChars)

    override def equals(k1: Keyword, k2: Keyword): Boolean = Arrays.==(k1.chars, k2.chars)
  }
}

@SerialVersionUID(1L)
class Keyword(@BeanProperty val name: String, s: String) extends AbstractSymbol {

  @BeanProperty
  val chars = Input.toIntArray(s)

  def this(name: String, chars: Array[Int]) {
    this()
    this.chars = chars
    this.name = name
  }

  def size(): Int = chars.length

  def getFirstTerminal(): Terminal = new java.lang.Character(chars(0))

  override def toString(): String = getName

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (!(obj.isInstanceOf[Keyword])) {
      return false
    }
    val other = obj.asInstanceOf[Keyword]
    Arrays.==(chars, other.chars)
  }

  override def hashCode(): Int = {
    externalHasher.hash(this, HashFunctions.defaulFunction())
  }

  override def addConditions(conditions: Collection[Condition]): Keyword = {
    val keyword = new Keyword(this.name, this.chars)
    keyword.conditions.addAll(this.conditions)
    keyword.conditions.addAll(conditions)
    keyword
  }
}
