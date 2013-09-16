package org.jgll_staged.lookup

import org.jgll_staged.grammar.Grammar
import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.sppf.IntermediateNode
import org.jgll_staged.sppf.ListSymbolNode
import org.jgll_staged.sppf.NonPackedNode
import org.jgll_staged.sppf.NonterminalSymbolNode
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
