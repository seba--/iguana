package org.jgll_staged.grammar.condition

import java.util.ArrayList
import java.util.List
import org.jgll_staged.grammar.Keyword
import org.jgll_staged.util.CollectionsUtil._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class KeywordCondition(`type`: ConditionType, keyword: Keyword) extends Condition(`type`) {

  @BeanProperty
  var keywords: List[Keyword] = new ArrayList()

  keywords.add(keyword)

  def this(`type`: ConditionType, keywords: List[Keyword]) {
    super(`type`)
    this.keywords = new ArrayList(keywords)
  }

  override def toString(): String = {
    `type`.toString + " " + listToString(keywords)
  }
}
