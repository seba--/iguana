package org.jgll_staged.grammar.condition

import java.util.List
import org.jgll_staged.grammar.Symbol
import org.jgll_staged.util.CollectionsUtil._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class ContextFreeCondition(`type`: ConditionType, @BeanProperty var symbols: List[_ <: Symbol])
    extends Condition(`type`) {

  override def toString(): String = {
    `type`.toString + " " + listToString(symbols)
  }
}
