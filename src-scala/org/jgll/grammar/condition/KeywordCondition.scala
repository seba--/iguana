package org.jgll.grammar.condition

import java.util.ArrayList
import org.jgll.grammar.Keyword
import org.jgll.util.CollectionsUtil._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
import org.jgll.grammar.condition.ConditionType.ConditionType

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class KeywordCondition(`type`: ConditionType, @BeanProperty val keywords: List[Keyword]) extends Condition(`type`) {


  def this(`type`: ConditionType, keyword: Keyword) {
    this(`type`, List(keyword))
  }

  override def toString(): String = {
    `type`.toString + " " + listToString(keywords)
  }
}
