package org.jgll.grammar.condition

import org.jgll.grammar.Keyword
import org.jgll.util.CollectionsUtil._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
import org.jgll.grammar.condition.ConditionType.ConditionType

import collection.mutable._

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class KeywordCondition(`type`: ConditionType, @BeanProperty val keywords: ListBuffer[Keyword]) extends Condition(`type`) {


  def this(`type`: ConditionType, keyword: Keyword) {
    this(`type`, ListBuffer(keyword))
  }

  override def toString(): String = {
    `type`.toString + " " + listToString(keywords)
  }
}
