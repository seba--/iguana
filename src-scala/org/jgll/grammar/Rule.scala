package org.jgll.grammar

import java.io.Serializable
import org.jgll.grammar.condition.Condition
import org.jgll.parser.HashFunctions
import Rule._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
import scala.collection.mutable.ListBuffer

//remove if not needed
import scala.collection.JavaConversions._

object Rule {

  class Builder(var head: Nonterminal) {

    var body: ListBuffer[Symbol] = ListBuffer()

    def addSymbol(symbol: Symbol): Builder = {
      body += symbol
      this
    }

    def build(): Rule = new Rule(this)
  }
}

@SerialVersionUID(1L)
class Rule(@BeanProperty val head: Nonterminal,
           _body: ListBuffer[_ <: Symbol] = ListBuffer[Symbol](),
           @BeanProperty val obj: AnyRef = null)
    extends Serializable {

  @BeanProperty
  val body: ListBuffer[Symbol] = ListBuffer() ++ _body

  @BeanProperty
  var conditions: ListBuffer[Condition] = ListBuffer()

  def this(head: Nonterminal, s: Symbol) {
    this(head, ListBuffer(s))
  }

  def this(head: Nonterminal, s: Symbol, obj: AnyRef) {
    this(head, ListBuffer(s), obj)
  }

  if (head == null) {
    throw new IllegalArgumentException("head cannot be null.")
  }

  if (body == null) {
    throw new IllegalArgumentException("Object cannot be null.")
  }

  for (s <- body if s == null) {
    throw new IllegalArgumentException("Body of a rule cannot have null symbols.")
  }

  private def this(builder: Builder) {
    this(builder.head, builder.body)
  }

  def size(): Int = body.size

  def addCondition(condition: Condition): Rule = {
    conditions += condition
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
      sb ++= s.toString ++= " "
    }
    sb.toString
  }

  override def equals(obj: Any): Boolean = {
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
