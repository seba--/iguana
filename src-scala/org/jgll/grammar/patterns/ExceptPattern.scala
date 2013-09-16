package org.jgll.grammar.patterns

import java.util.List
import org.jgll.grammar.Nonterminal
import org.jgll.grammar.Symbol
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class ExceptPattern(nonteriminal: Nonterminal, 
    parent: List[Symbol], 
    position: Int, 
    child: List[Symbol]) extends AbstractPattern(nonteriminal, parent, position, child)
