package org.jgll.util

import org.jgll.traversal.PositionInfoTrait
import scala.virtualization.lms.common._
import scala.Array
import java.io.{BufferedInputStream, InputStream}


trait InputTrait {
    self: Base
     with ScalaOpsPkg
     with BooleanOps
     with Structs
     with Equal
     with LiftPrimitives
     with LiftBoolean
     with PositionInfoTrait
  =>
  object Input {
    def fromIntArray(input: Rep[Array[Int]]): Input = new Input(input)

    // STAGING: how to do Strings?

  //  def fromString(s: Rep[String]): Input = {
  //    val input = NewArray[Int](s.length + 1)
  //    for (i <- 0 until s.length) {
  //      input(i) = s.codePointAt(i)
  //    }
  //    input(s.length) = 0
  //    new Input(input)
  //  }

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
      val length: Int = s.codePointCount(0, s.length)
      val array: scala.Array[Int] = scala.Array[Int](length)
      for (i <- Range(0, length)) {
        array(i) = s.codePointAt(i)
      }
      array
    }
  }


  type LineColumn = Record {
    val lineNumber: Int
    val columnNumber: Int
  }
  object LineColumn {
    def create(lnum: Rep[Int], cnum: Rep[Int]) = new LineColumn {
        val lineNumber: Int = lnum
        val columnNumber: Int = cnum
      }

    def create(lc: Rep[LineColumn]) = new LineColumn {
      val lineNumber: Int = lc.lineNumber
      val columnNumber: Int = lc.columnNumber
    }

    def equal(lc1: LineColumn, lc2: LineColumn) =
      lc1.lineNumber == lc2.lineNumber && lc1.columnNumber == lc2.columnNumber

    def toString(lc: LineColumn) =
      "(" + lc.lineNumber + ":" + lc.columnNumber + ")"
  }


  import Input._

  class Input (private var input: Rep[Array[Int]]) {

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
        0
      }
      else
        lineColumns(index).columnNumber
    }

    def equal(other: Input): Rep[Boolean] = {
      if (input.length != other.input)
        false
      else {
        var i = 0
        var ok = true
        while (ok && i < input.length) {
          if (input(i) != other.input(i))
            ok = false
          i += 1
        }
        ok
      }
    }

    def getPositionInfo(leftExtent: Rep[Int], rightExtent: Rep[Int]): Rep[PositionInfo] = {
      PositionInfo.create(leftExtent,
                          rightExtent - leftExtent,
                          getLineNumber(leftExtent),
                          getColumnNumber(leftExtent),
                          getLineNumber(rightExtent),
                          getColumnNumber(rightExtent))
    }

    private def calculateLineLengths() {
      var lineNumber: Rep[Int] = 1
      var columnNumber: Rep[Int] = 1
      if (input.length == 1) {
        lineColumns(0) = LineColumn.create(lineNumber, columnNumber)
      }
      else {
        for (i <- 0 until input.length - 1) {
          lineColumns(i) = LineColumn.create(lineNumber, columnNumber)
          if (input(i) == '\n') {
            lineNumber += 1
            columnNumber = 1
          } else if (input(i) == '\r') {
            columnNumber = 1
          } else {
            columnNumber += 1
          }
        }
        lineColumns(input.length - 1) = LineColumn.create(lineColumns(input.length - 2))
      }
    }

//    override def toString(): String = {
//      val charList = ListBuffer[Character]()
//      for (i <- input) {
//        val chars = Character.toChars(i)
//        for (c <- chars) {
//          charList += c
//        }
//      }
//      val sb = new StringBuilder()
//      for (c <- charList) {
//        sb.append(c)
//      }
//      sb.toString
//    }

    def isEndOfLine(currentInputIndex: Rep[Int]): Rep[Boolean ]= {
      input(currentInputIndex) == 0 || lineColumns(currentInputIndex + 1).columnNumber == 0
    }

    def isStartOfLine(currentInputIndex: Rep[Int]): Rep[Boolean] = {
      currentInputIndex == 0 || lineColumns(currentInputIndex).columnNumber == 0
    }
  }
}