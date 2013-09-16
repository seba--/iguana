package org.jgll.grammar.condition

import java.util.Arrays
import org.jgll.grammar.Keyword
import org.jgll.grammar.Symbol
import org.jgll.grammar.Terminal
import ConditionType._

//remove if not needed
import scala.collection.JavaConversions._

object ConditionFactory {

  def follow[T <: Symbol](symbols: List[T]): Condition = {
    createCondition(ConditionType.FOLLOW, symbols)
  }

  def notFollow[T <: Symbol](symbols: List[T]): Condition = {
    createCondition(ConditionType.NOT_FOLLOW, symbols)
  }

  def precede[T <: Symbol](symbols: List[T]): Condition = {
    createCondition(ConditionType.PRECEDE, symbols)
  }

  def notPrecede[T <: Symbol](symbols: List[T]): Condition = {
    createCondition(ConditionType.NOT_PRECEDE, symbols)
  }

  def `match`[T <: Symbol](symbols: List[T]): Condition = {
    createCondition(ConditionType.MATCH, symbols)
  }

  def notMatch[T <: Symbol](symbols: List[T]): Condition = {
    createCondition(ConditionType.NOT_MATCH, symbols)
  }

  private def allTerminal[T <: Symbol](symbols: List[T]): Boolean = {
    symbols.find(x => !x.isInstanceOf[Terminal]).isEmpty
  }

  private def createCondition[T <: Symbol](`type`: ConditionType, symbols: List[T]): Condition = {
    if (allKeywords(symbols)) {
      new KeywordCondition(`type`, symbols.asInstanceOf[List[Keyword]])
    } else if (allTerminal(symbols)) {
      new TerminalCondition(`type`, symbols.asInstanceOf[List[Terminal]])
    } else {
      new ContextFreeCondition(`type`, symbols)
    }
  }

  private def allKeywords[T <: Symbol](symbols: List[T]): Boolean = {
    symbols.find(x => !(x.isInstanceOf[Keyword])).isEmpty
  }

  def endOfLine(): Condition = {
    new PositionalCondition(END_OF_LINE)
  }

  def startOfLine(): Condition = {
    new PositionalCondition(START_OF_LINE)
  }
}
