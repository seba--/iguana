package org.jgll.util

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Arrays
import org.jgll.traversal.PositionInfo
import scala.reflect.{BeanProperty, BooleanBeanProperty}
import scala.virtualization.lms.common._
import scala.Array
import scala.collection.mutable.ListBuffer
import org.jgll.util.lms.lift.LineColumnOps

import Input._

object Input {
  class LineColumn(@BeanProperty lineNumber: Int, @BeanProperty columnNumber: Int) {

    def this(lineColumn: LineColumn) {
      this(lineColumn.lineNumber, lineColumn.columnNumber)
    }

    override def toString(): String = {
      "(" + lineNumber + ":" + columnNumber + ")"
    }

    override def equals(obj: Any): Boolean = {
      if (!(obj.isInstanceOf[LineColumn])) {
        return false
      }
      val other = obj.asInstanceOf[LineColumn]
      lineNumber == other.lineNumber && columnNumber == other.columnNumber
    }
  }
}

trait InputTrait
  extends BaseExp
     with ScalaOpsPkg
     with BooleanOpsExp
     with Equal
     with LineColumnOps
     with LiftPrimitives
     with LiftBoolean
{
  def fromIntArray(input: Rep[Array[Int]]): Input = new Input(input)

  def fromString(s: Rep[String]): Input = {
    val input = NewArray[Int](s.length + 1)
    for (i <- 0 until s.length) {
      input(i) = s.codePointAt(i)
    }
    input(s.length) = 0
    new Input(input)
  }

//  def fromPath(path: String): Input = fromString(readTextFromFile(path))

//  private def readTextFromFile(path: String): String = readTextFromFile(new File(path))

//  private def readTextFromFile(file: File): String = {
//    val sb = new StringBuilder()
//    val in = new BufferedInputStream(new FileInputStream(file))
//    var c = 0
//    def setC(i: Int) = { c = i; c }
//
//    while (setC(in.read()) != -1) {
//      sb.append(c.toChar)
//    }
//    in.close()
//    sb.toString
//  }

//  def read(is: InputStream): String = {
//    val sb = new StringBuilder()
//    val in = new BufferedInputStream(is)
//    var c = 0
//    def setC(i: Int) = { c = i; c }
//
//    while (setC(in.read()) != -1) {
//      sb.append(c.toChar)
//    }
//    in.close()
//    sb.toString
//  }

  def toIntArray(s: String): Array[Int] = {
    val array = scala.Array.ofDim[Int](s.codePointCount(0, s.length))
    for (i <- 0 until array.length) {
      array(i) = s.codePointAt(i)
    }
    array
  }

  class Input private (private var input: Rep[Array[Int]]) {

    private var lineColumns: Rep[Array[LineColumn]] = NewArray[LineColumn](input.length)

    calculateLineLengths()

    def charAt(index: Rep[Int]): Rep[Int] = input(index)

    def size(): Rep[Int] = input.length

    def subInput(start: Rep[Int], end: Rep[Int]): Rep[Array[Int]] = {
      val length = end - start + 1
      val subInput = NewArray[Int](length)
      array_copy(input, start, subInput, 0, length)
      subInput
    }

    def `match`(start: Rep[Int], end: Rep[Int], target: String): Rep[Boolean] = `match`(start, end, toIntArray(target))

    def `match`(start: Rep[Int], end: Rep[Int], target: Array[Int]): Rep[Boolean] = {
      if (target.length != end - start) {
        false
      }
      else {
//        var i = 0
//        // STAGE: need ot lift break?
//        var break: Rep[Boolean] = false
//        // STAGE: replace while loop by recursive function to enable unrolling
//        while (!break && i < target.length) {
//          if (target(i) != input(start + i)) {
//            break = true
//          }
//          else
//            i += 1
//        }
//        !break
        def loop(i: Int): Rep[Boolean] =
          if (i >= target.length)
            true
          else if (target(i) != input(start + i))
            false
          else loop(i+1)

        loop(0)
      }
    }

    def `match`(from: Rep[Int], target: String): Rep[Boolean] = `match`(from, toIntArray(target))

    def matchBackward(start: Rep[Int], target: String): Rep[Boolean] = {
      matchBackward(start, toIntArray(target))
    }

    def matchBackward(start: Rep[Int], target: Array[Int]): Rep[Boolean] = {
      if (start - target.length < 0) {
        false
      }
      else {
//        var i = target.length - 1
//        var j = start - 1
//        while (i >= 0) {
//          if (target(i) != input(j)) {
//            return false
//          }
//          i -= 1
//          j -= 1
//        }
//        true

        def loop(i: Int, j: Rep[Int]): Rep[Boolean] =
          if (i < 0)
            true
          else if (target(i) != input(j))
            false
          else
            loop(i - 1, j - 1)

        loop(target.length - 1, start - 1)
      }
    }

    def `match`(from: Rep[Int], target: Array[Int]): Rep[Boolean] = {
      if (target.length > size - from)
        false
      else {
//        var i = 0
//        while (i < target.length) {
//          if (target(i) != input(from + i)) {
//            return false
//          }
//          i += 1
//        }
//        true

        def loop(i: Int): Rep[Boolean] =
          if (i >= target.length)
            true
          else if (target(i) != input(from + i))
            false
          else
            loop(i + 1)

        loop(0)
      }
    }

    def getLineNumber(index: Rep[Int]): Rep[Int] = {
      if (index < 0 || index >= lineColumns.length) {
        0
      }
      else
        lineColumns(index).lineNumber
    }

    def getColumnNumber(index: Rep[Int]): Rep[Int] = {
      if (index < 0 || index >= lineColumns.length) {
        return 0
      }
      lineColumns(index).columnNumber
    }

    override def equals(obj: Any): Rep[Boolean] = {
      if (!(obj.isInstanceOf[Input])) {
        false
      }
      else {
        val other = obj.asInstanceOf[Input]
        Arrays.equals(input, other.input)
      }
    }

    def getPositionInfo(leftExtent: Int, rightExtent: Int): PositionInfo = {
      new PositionInfo(leftExtent,
                       rightExtent - leftExtent,
                       getLineNumber(leftExtent),
                       getColumnNumber(leftExtent),
                       getLineNumber(rightExtent),
                       getColumnNumber(rightExtent))
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
      val charList = ListBuffer[Character]()
      for (i <- input) {
        val chars = Character.toChars(i)
        for (c <- chars) {
          charList += c
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
}