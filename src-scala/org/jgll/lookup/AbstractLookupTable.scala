package org.jgll.lookup

import org.jgll.grammar.Grammar
import org.jgll.grammar.slot.GrammarSlot
import org.jgll.grammar.slot.HeadGrammarSlot
import org.jgll.sppf.IntermediateNode
import org.jgll.sppf.ListSymbolNode
import org.jgll.sppf.NonPackedNode
import org.jgll.sppf.NonterminalSymbolNode
//remove if not needed
import scala.collection.JavaConversions._

abstract class AbstractLookupTable(protected val grammar: Grammar) extends LookupTable {

  protected val slotsSize = grammar.getGrammarSlots.size

  protected def createNonPackedNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Int): NonPackedNode = {
    var key: NonPackedNode = null
    if (slot.isInstanceOf[HeadGrammarSlot]) {
      val head = slot.asInstanceOf[HeadGrammarSlot]
      key = if (head.getNonterminal.isEbnfList) new ListSymbolNode(slot, leftExtent, rightExtent) else new NonterminalSymbolNode(slot, 
        leftExtent, rightExtent)
    } else {
      key = new IntermediateNode(slot, leftExtent, rightExtent)
    }
    key
  }
}
