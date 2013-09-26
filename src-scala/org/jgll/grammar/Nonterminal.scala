package org.jgll.grammar

import org.jgll.grammar.condition.Condition
import scala.reflect.{BeanProperty, BooleanBeanProperty}

import scala.collection.mutable._

@SerialVersionUID(1L)
class Nonterminal(@BeanProperty val name: String,
                  private val ebnfList: Boolean = false,
                  conditions: ListBuffer[Condition] = ListBuffer())
    extends AbstractSymbol(conditions) {

  @BooleanBeanProperty
  var collapsible: Boolean = _

  def isEbnfList(): Boolean = {
    if (ebnfList == true) {
      return true
    } else {
      if (name.startsWith("List")) {
        return true
      }
    }
    false
  }

  override def addConditions(conditions: ListBuffer[Condition]): Symbol = {
    val nonterminal = new Nonterminal(this.name)
    nonterminal.conditions ++= this.conditions
    nonterminal.conditions ++= conditions
    nonterminal
  }

  override def toString(): String = name

  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[Nonterminal])) {
      return false
    }
    val other = obj.asInstanceOf[Nonterminal]
    name == other.name
  }

  override def hashCode(): Int = name.hashCode
}
