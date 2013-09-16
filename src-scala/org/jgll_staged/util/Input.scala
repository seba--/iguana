package org.jgll_staged.util

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Arrays
import java.util.List
import org.jgll_staged.traversal.PositionInfo
import Input._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object Input {

  def fromString(s: String): Input = {
    val input = Array.ofDim[Int](s.length + 1)
    for (i <- 0 until s.length) {
      input(i) = s.codePointAt(i)
    }
    input(s.length) = 0
    new Input(input)
  }

  def fromIntArray(input: Array[Int]): Input = new Input(input)

  def fromPath(path: String): Input = fromString(readTextFromFile(path))

  private def readTextFromFile(path: String): String = readTextFromFile(new File(path))

  private def readTextFromFile(file: File): String = {
    val sb = new StringBuilder()
    val in = new BufferedInputStream(new FileInputStream(file))
    var c = 0
    def setC(i: Int) = { c = i; c }

    while (setC(in.read()) != -1) {
      sb.append(c.toChar)
    }
    in.close()
    sb.toString
  }

  def read(is: InputStream): String = {
    val sb = new StringBuilder()
    val in = new BufferedInputStream(is)
    var c = 0
    def setC(i: Int) = { c = i; c }

    while (setC(in.read()) != -1) {
      sb.append(c.toChar)
    }
    in.close()
    sb.toString
  }

  def toIntArray(s: String): Array[Int] = {
    val array = Array.ofDim[Int](s.codePointCount(0, s.length))
    for (i <- 0 until array.length) {
      array(i) = s.codePointAt(i)
    }
    array
  }

  private class LineColumn(@BeanProperty var lineNumber: Int, @BeanProperty var columnNumber: Int)
      {

    def this(lineColumn: LineColumn) {
      this(lineColumn.lineNumber, lineColumn.columnNumber)
    }

    override def toString(): String = {
      "(" + lineNumber + ":" + columnNumber + ")"
    }

    override def equals(obj: Any): Boolean = {
      if (this == obj) {
        return true
      }
      if (!(obj.isInstanceOf[LineColumn])) {
        return false
      }
      val other = obj.asInstanceOf[LineColumn]
      lineNumber == other.lineNumber && columnNumber == other.columnNumber
    }
  }
}

class Input private (private var input: Array[Int]) {

  private var lineColumns: Array[LineColumn] = new Array[LineColumn](input.length)

  calculateLineLengths()

  def charAt(index: Int): Int = input(index)

  def size(): Int = input.length

  def subInput(start: Int, end: Int): Array[Int] = {
    val length = end - start + 1
    val subInput = Array.ofDim[Int](length)
    System.arraycopy(input, start, subInput, 0, length)
    subInput
  }

  def `match`(start: Int, end: Int, target: String): Boolean = `match`(start, end, toIntArray(target))

  def `match`(start: Int, end: Int, target: Array[Int]): Boolean = {
    if (target.length != end - start) {
      return false
    }
    var i = 0
    while (i < target.length) {
      if (target(i) != input(start + i)) {
        return false
      }
      i += 1
    }
    true
  }

  def `match`(from: Int, target: String): Boolean = `match`(from, toIntArray(target))

  def matchBackward(start: Int, target: String): Boolean = {
    matchBackward(start, toIntArray(target))
  }

  def matchBackward(start: Int, target: Array[Int]): Boolean = {
    if (start - target.length < 0) {
      return false
    }
    var i = target.length - 1
    var j = start - 1
    while (i >= 0) {
      if (target(i) != input(j)) {
        return false
      }
      i -= 1
      j -= 1
    }
    true
  }

  def `match`(from: Int, target: Array[Int]): Boolean = {
    if (target.length > size - from) {
      return false
    }
    var i = 0
    while (i < target.length) {
      if (target(i) != input(from + i)) {
        return false
      }
      i += 1
    }
    true
  }

  def getLineNumber(index: Int): Int = {
    if (index < 0 || index >= lineColumns.length) {
      return 0
    }
    lineColumns(index).getLineNumber
  }

  def getColumnNumber(index: Int): Int = {
    if (index < 0 || index >= lineColumns.length) {
      return 0
    }
    lineColumns(index).getColumnNumber
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (!(obj.isInstanceOf[Input])) {
      return false
    }
    val other = obj.asInstanceOf[Input]
    Arrays.equals(input, other.input)
  }

  def getPositionInfo(leftExtent: Int, rightExtent: Int): PositionInfo = {
    new PositionInfo(leftExtent, rightExtent - leftExtent, getLineNumber(leftExtent), getColumnNumber(leftExtent), 
      getLineNumber(rightExtent), getColumnNumber(rightExtent))
  }

  private def calculateLineLengths() {
    var lineNumber = 1
    var columnNumber = 1
    if (input.length == 1) {
      lineColumns(0) = new LineColumn(lineNumber, columnNumber)
      return
    }
    for (i <- 0 until input.length - 1) {
      lineColumns(i) = new LineColumn(lineNumber, columnNumber)
      if (input(i) == '\n') {
        lineNumber += 1
        columnNumber = 1
      } else if (input(i) == '\r') {
        columnNumber = 1
      } else {
        columnNumber += 1
      }
    }
    lineColumns(input.length - 1) = new LineColumn(lineColumns(input.length - 2))
  }

  override def toString(): String = {
    val charList = new ArrayList[Character]()
    for (i <- input) {
      val chars = Character.toChars(i)
      for (c <- chars) {
        charList.add(c)
      }
    }
    val sb = new StringBuilder()
    for (c <- charList) {
      sb.append(c)
    }
    sb.toString
  }

  def isEndOfLine(currentInputIndex: Int): Boolean = {
    input(currentInputIndex) == 0 || 
      lineColumns(currentInputIndex + 1).columnNumber == 0
  }

  def isStartOfLine(currentInputIndex: Int): Boolean = {
    currentInputIndex == 0 || lineColumns(currentInputIndex).columnNumber == 0
  }
}
