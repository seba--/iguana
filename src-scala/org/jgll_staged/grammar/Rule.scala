package org.jgll_staged.grammar

import java.io.Serializable
import java.util.ArrayList
import java.util.Arrays
import java.util.List
import org.jgll_staged.grammar.condition.Condition
import org.jgll_staged.parser.HashFunctions
import Rule._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object Rule {

  class Builder(private var head: Nonterminal) {

    private var body: List[Symbol] = new ArrayList()

    def addSymbol(symbol: Symbol): Builder = {
      body.add(symbol)
      this
    }

    def build(): Rule = new Rule(this)
  }
}

@SerialVersionUID(1L)
class Rule(@BeanProperty val head: Nonterminal, body: List[_ <: Symbol], @BeanProperty val `object`: AnyRef)
    extends Serializable {

  @BeanProperty
  val body = new ArrayList(body)

  @BeanProperty
  var conditions: List[Condition] = new ArrayList()

  if (head == null) {
    throw new IllegalArgumentException("head cannot be null.")
  }

  if (body == null) {
    throw new IllegalArgumentException("Object cannot be null.")
  }

  for (s <- body if s == null) {
    throw new IllegalArgumentException("Body of a rule cannot have null symbols.")
  }

  def this(head: Nonterminal) {
    this(head, new ArrayList[Symbol](), null)
  }

  private def this(builder: Builder) {
    this(builder.head, builder.body)
  }

  def this(head: Nonterminal, body: Symbol*) {
    this(head, Arrays.asList(body:_*), null)
  }

  def this(head: Nonterminal, body: List[_ <: Symbol]) {
    this(head, body, null)
  }

  def size(): Int = body.size

  def addCondition(condition: Condition): Rule = {
    conditions.add(condition)
    this
  }

  def getSymbolAt(index: Int): Symbol = {
    if (index > body.size) {
      throw new IllegalArgumentException(index + " cannot be greater than " + body.size)
    }
    body.get(index)
  }

  override def toString(): String = {
    val sb = new StringBuilder()
    sb.append(head).append(" ::= ")
    for (s <- body) {
      sb.append(s).append(" ")
    }
    sb.toString
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (!(obj.isInstanceOf[Rule])) {
      return false
    }
    val other = obj.asInstanceOf[Rule]
    head == other.head && body == other.body
  }

  override def hashCode(): Int = {
    HashFunctions.defaulFunction().hash(head.hashCode, body.hashCode)
  }
}
