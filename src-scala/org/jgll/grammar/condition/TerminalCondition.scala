package org.jgll.grammar.condition

import java.util.List
import org.jgll.grammar.Terminal
import org.jgll.util.CollectionsUtil._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
import org.jgll.grammar.condition.ConditionType._

//remove if not needed
import scala.collection.JavaConversions._


@SerialVersionUID(1L)
class TerminalCondition(`type`: ConditionType, @BeanProperty var terminals: List[Terminal])
    extends Condition(`type`) {

  override def toString(): String = {
    `type`.toString + " " + listToString(terminals)
  }
}
