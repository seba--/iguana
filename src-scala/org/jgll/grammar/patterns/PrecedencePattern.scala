package org.jgll.grammar.patterns

import java.io.Serializable
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Symbol

import collection.mutable._

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class PrecedencePattern(nonteriminal: Nonterminal, 
    parent: ListBuffer[Symbol],
    position: Int, 
    child: ListBuffer[Symbol]) extends AbstractPattern(nonteriminal, parent, position, child) with Serializable {

  def isDirect(): Boolean = nonterminal == parent.get(position)

  def isParentBinary(): Boolean = {
    nonterminal == parent.get(0) && nonterminal == parent.get(parent.size - 1)
  }

  def isChildBinary(): Boolean = {
    nonterminal == child.get(0) && nonterminal == child.get(child.size - 1)
  }

  def isLeftMost(): Boolean = position == 0

  def isRightMost(): Boolean = position == child.size - 1
}
