package org.jgll.grammar.condition

import java.util.Arrays
import org.jgll.grammar.Keyword
import org.jgll.grammar.Symbol
import org.jgll.grammar.Terminal
import ConditionType._
import scala.collection.mutable.ListBuffer

//remove if not needed
import scala.collection.JavaConversions._

object ConditionFactory {

  def follow[T <: Symbol](symbols: ListBuffer[T]): Condition = {
    createCondition(ConditionType.FOLLOW, symbols)
  }

  def notFollow[T <: Symbol](symbols: ListBuffer[T]): Condition = {
    createCondition(ConditionType.NOT_FOLLOW, symbols)
  }

  def notFollow[T <: Symbol](symbols: T*): Condition = {
    createCondition(ConditionType.NOT_FOLLOW, ListBuffer() ++ symbols)
  }

  def precede[T <: Symbol](symbols: ListBuffer[T]): Condition = {
    createCondition(ConditionType.PRECEDE, symbols)
  }

  def notPrecede[T <: Symbol](symbols: ListBuffer[T]): Condition = {
    createCondition(ConditionType.NOT_PRECEDE, symbols)
  }

  def `match`[T <: Symbol](symbols: ListBuffer[T]): Condition = {
    createCondition(ConditionType.MATCH, symbols)
  }

  def notMatch[T <: Symbol](symbols: ListBuffer[T]): Condition = {
    createCondition(ConditionType.NOT_MATCH, symbols)
  }

  def notMatch[T <: Symbol](symbols: T*): Condition = {
    createCondition(ConditionType.NOT_MATCH, ListBuffer() ++ symbols)
  }

  private def allTerminal[T <: Symbol](symbols: ListBuffer[T]): Boolean = {
    symbols.find(x => !x.isInstanceOf[Terminal]).isEmpty
  }

  private def createCondition[T <: Symbol](`type`: ConditionType, symbols: ListBuffer[T]): Condition = {
    if (allKeywords(symbols)) {
      new KeywordCondition(`type`, symbols.asInstanceOf[ListBuffer[Keyword]])
    } else if (allTerminal(symbols)) {
      new TerminalCondition(`type`, symbols.asInstanceOf[ListBuffer[Terminal]])
    } else {
      new ContextFreeCondition(`type`, symbols)
    }
  }

  private def allKeywords[T <: Symbol](symbols: ListBuffer[T]): Boolean = {
    symbols.find(x => !(x.isInstanceOf[Keyword])).isEmpty
  }

  def endOfLine(): Condition = {
    new PositionalCondition(END_OF_LINE)
  }

  def startOfLine(): Condition = {
    new PositionalCondition(START_OF_LINE)
  }
}
