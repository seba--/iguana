package org.jgll.grammar

import EOF._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
object EOF  extends Character(0) {

  override def toString(): String = "$"
}
