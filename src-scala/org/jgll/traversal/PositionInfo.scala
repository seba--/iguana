package org.jgll.traversal

import scala.reflect.{BeanProperty, BooleanBeanProperty}
import scala.virtualization.lms.common.Structs

trait PositionInfoTrait extends Structs {
  type PositionInfo = Record {
    val start: Int
    val offset: Int
    val lineNumber: Int
    val columnNumber: Int
    val endLineNumber: Int
    val endColumnNumber: Int
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
