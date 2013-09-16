package org.jgll_staged.grammar

import java.util.Collection
import org.jgll_staged.grammar.condition.Condition
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class Nonterminal(@BeanProperty val name: String, private val ebnfList: Boolean)
    extends AbstractSymbol {

  @BooleanBeanProperty
  var collapsible: Boolean = _

  def this(name: String) {
    this(name, false)
  }

  def this(name: String, ebnfList: Boolean, conditions: java.lang.Iterable[Condition]) {
    super(conditions)
    this.name = name
    this.ebnfList = ebnfList
  }

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

  override def addConditions(conditions: Collection[Condition]): Symbol = {
    val nonterminal = new Nonterminal(this.name)
    nonterminal.conditions.addAll(this.conditions)
    nonterminal.conditions.addAll(conditions)
    nonterminal
  }

  override def toString(): String = name

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (!(obj.isInstanceOf[Nonterminal])) {
      return false
    }
    val other = obj.asInstanceOf[Nonterminal]
    name == other.name
  }

  override def hashCode(): Int = name.hashCode
}
