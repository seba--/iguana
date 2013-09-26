package org.jgll.grammar.condition

import org.jgll.grammar.{Symbol,KeywordTrait,TerminalTrait}
import ConditionType._
import scala.collection.mutable.ListBuffer

trait ConditionFactory {
  self: KeywordConditionTrait
   with KeywordTrait
   with TerminalTrait
   with TerminalConditionTrait =>
  object ConditionFactory {
  
    def follow(symbols: ListBuffer[Symbol]): Condition = {
      createCondition(ConditionType.FOLLOW, symbols)
    }
  
    def notFollow(symbols: ListBuffer[Symbol]): Condition = {
      createCondition(ConditionType.NOT_FOLLOW, symbols)
    }
  
    def notFollow(symbols: Symbol*): Condition = {
      createCondition(ConditionType.NOT_FOLLOW, ListBuffer() ++ symbols)
    }
  
    def precede(symbols: ListBuffer[Symbol]): Condition = {
      createCondition(ConditionType.PRECEDE, symbols)
    }
  
    def notPrecede(symbols: ListBuffer[Symbol]): Condition = {
      createCondition(ConditionType.NOT_PRECEDE, symbols)
    }
  
    def `match`(symbols: ListBuffer[Symbol]): Condition = {
      createCondition(ConditionType.MATCH, symbols)
    }
  
    def notMatch(symbols: ListBuffer[Symbol]): Condition = {
      createCondition(ConditionType.NOT_MATCH, symbols)
    }
  
    def notMatch(symbols: Symbol*): Condition = {
      createCondition(ConditionType.NOT_MATCH, ListBuffer() ++ symbols)
    }
  
    private def allTerminal(symbols: ListBuffer[Symbol]): Boolean = {
      symbols.find(x => !x.isInstanceOf[Terminal]).isEmpty
    }
  
    private def createCondition(`type`: ConditionType, symbols: ListBuffer[Symbol]): Condition = {
      if (allKeywords(symbols)) {
        new KeywordCondition(`type`, symbols.asInstanceOf[ListBuffer[Keyword]])
      } else if (allTerminal(symbols)) {
        new TerminalCondition(`type`, symbols.asInstanceOf[ListBuffer[Terminal]])
      } else {
        new ContextFreeCondition(`type`, symbols)
      }
    }
  
    private def allKeywords(symbols: ListBuffer[Symbol]): Boolean = {
      symbols.find(x => !(x.isInstanceOf[Keyword])).isEmpty
    }
  
    def endOfLine(): Condition = {
      new PositionalCondition(END_OF_LINE)
    }
  
    def startOfLine(): Condition = {
      new PositionalCondition(START_OF_LINE)
    }
  }
}