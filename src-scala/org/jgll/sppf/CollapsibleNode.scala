package org.jgll.sppf

import org.jgll.grammar.slot.GrammarSlot
//remove if not needed
import scala.collection.JavaConversions._

class CollapsibleNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Int) extends NonterminalSymbolNode(slot, 
  leftExtent, rightExtent)
