package org.jgll.sppf

import org.jgll.grammar.slot.GrammarSlotTrait

trait CollapsibleNodeTrait {
  self: GrammarSlotTrait
   with NonterminalSymbolNodeTrait =>
  class CollapsibleNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Rep[Int])
    extends NonterminalSymbolNode(slot, leftExtent, rightExtent)
}
