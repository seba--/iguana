package org.jgll.grammar.patterns

import java.io.Serializable
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Symbol

import collection.mutable._

@SerialVersionUID(1L)
class PrecedencePattern(nonterminal: Nonterminal,
    parent: ListBuffer[Symbol],
    position: Int, 
    child: ListBuffer[Symbol]) extends AbstractPattern(nonterminal, parent, position, child) with Serializable {

  def isDirect(): Boolean = nonterminal == parent(position)

  def isParentBinary(): Boolean = {
    nonterminal == parent(0) && nonterminal == parent(parent.size - 1)
  }

  def isChildBinary(): Boolean = {
    nonterminal == child(0) && nonterminal == child(child.size - 1)
  }

  def isLeftMost(): Boolean = position == 0

  def isRightMost(): Boolean = position == child.size - 1
}
