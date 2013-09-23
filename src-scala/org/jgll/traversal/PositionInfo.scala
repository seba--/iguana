package org.jgll.traversal

import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

class PositionInfo(@BeanProperty val start: Int, 
    @BeanProperty val offset: Int, 
    lineNumber: Int, 
    columnNumber: Int, 
    @BeanProperty val endLineNumber: Int, 
    @BeanProperty val endColumnNumber: Int) {

  def getLineNumber(): Int = lineNumber

  def getColumn(): Int = columnNumber
}
