package org.jgll.grammar.patterns

import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Symbol
import scala.collection.mutable.ListBuffer

@SerialVersionUID(1L)
class ExceptPattern(nonteriminal: Nonterminal, 
    parent: ListBuffer[Symbol],
    position: Int, 
    child: ListBuffer[Symbol]) extends AbstractPattern(nonteriminal, parent, position, child)
