package org.jgll.traversal

import scala.reflect.{BeanProperty, BooleanBeanProperty}
import scala.virtualization.lms.common.Structs

//remove if not needed
import scala.collection.JavaConversions._

trait PositionInfoTrait extends Structs {
  trait PositionInfo extends Record {
    @BeanProperty val start: Int
    @BeanProperty val offset: Int
    val lineNumber: Int
    val columnNumber: Int
    @BeanProperty val endLineNumber: Int
    @BeanProperty val endColumnNumber: Int
  }

  object PositionInfo {
    def create(s: Rep[Int], o: Rep[Int], ln: Rep[Int], cn: Rep[Int], eln: Rep[Int], ecn: Rep[Int]) = new PositionInfo {
      val endColumnNumber: Int = ecn
      val columnNumber: Int = cn
      val lineNumber: Int = ln
      val offset: Int = o
      val start: Int = s
      val endLineNumber: Int = eln
    }

    }
}
