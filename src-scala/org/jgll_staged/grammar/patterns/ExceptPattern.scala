package org.jgll_staged.grammar.patterns

import java.util.List
import org.jgll_staged.grammar.Nonterminal
import org.jgll_staged.grammar.Symbol
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class ExceptPattern(nonteriminal: Nonterminal, 
    parent: List[Symbol], 
    position: Int, 
    child: List[Symbol]) extends AbstractPattern(nonteriminal, parent, position, child)
