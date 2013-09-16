package org.jgll_staged.grammar.condition

import java.util.Arrays
import java.util.List
import org.jgll_staged.grammar.Keyword
import org.jgll_staged.grammar.Symbol
import org.jgll_staged.grammar.Terminal
//remove if not needed
import scala.collection.JavaConversions._

object ConditionFactory {

  @SafeVarargs
  def follow[T <: Symbol](symbols: T*): Condition = {
    createCondition(ConditionType.FOLLOW, symbols)
  }

  @SafeVarargs
  def notFollow[T <: Symbol](symbols: T*): Condition = {
    createCondition(ConditionType.NOT_FOLLOW, symbols)
  }

  def notFollow[T <: Symbol](symbols: List[T]): Condition = {
    createCondition(ConditionType.NOT_FOLLOW, symbols)
  }

  @SafeVarargs
  def precede[T <: Symbol](symbols: T*): Condition = {
    createCondition(ConditionType.PRECEDE, symbols)
  }

  @SafeVarargs
  def notPrecede[T <: Symbol](symbols: T*): Condition = {
    createCondition(ConditionType.NOT_PRECEDE, symbols)
  }

  @SafeVarargs
  def `match`[T <: Symbol](symbols: T*): Condition = {
    createCondition(ConditionType.MATCH, symbols)
  }

  @SafeVarargs
  def notMatch[T <: Symbol](symbols: T*): Condition = {
    createCondition(ConditionType.NOT_MATCH, symbols)
  }

  private def allTerminal[T <: Symbol](symbols: List[T]): Boolean = {
    symbols.find(!(_.isInstanceOf[Terminal])).map(false)
      .getOrElse(true)
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

  private def createCondition[T <: Symbol](`type`: ConditionType, symbols: T*): Condition = {
    createCondition(`type`, Arrays.asList(symbols:_*))
  }

  private def allKeywords[T <: Symbol](symbols: List[T]): Boolean = {
    symbols.find(!(_.isInstanceOf[Keyword])).map(false)
      .getOrElse(true)
  }

  def endOfLine(): Condition = {
    new PositionalCondition(ConditionType.END_OF_LINE)
  }

  def startOfLine(): Condition = {
    new PositionalCondition(ConditionType.START_OF_LINE)
  }
}
