package org.jgll_staged.grammar.condition

import java.util.ArrayList
import java.util.List
import org.jgll_staged.grammar.Keyword
import org.jgll_staged.util.CollectionsUtil._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
import org.jgll_staged.grammar.condition.ConditionType.ConditionType

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class KeywordCondition(`type`: ConditionType, @BeanProperty keywords: List[Keyword]) extends Condition(`type`) {


  def this(`type`: ConditionType, keyword: Keyword) {
    this(`type`, List(keyword))
  }

  override def toString(): String = {
    `type`.toString + " " + listToString(keywords)
  }
}
