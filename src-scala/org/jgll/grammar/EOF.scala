package org.jgll.grammar

import EOF._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object EOF {

  @BeanProperty
  lazy val instance = new EOF()
}

@SerialVersionUID(1L)
class EOF private () extends Character(0) {

  override def toString(): String = "$"
}
