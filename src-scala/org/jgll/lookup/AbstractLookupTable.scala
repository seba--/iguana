package org.jgll.lookup

import org.jgll.grammar.GrammarTrait
import org.jgll.grammar.slot.{HeadGrammarSlotTrait, GrammarSlotTrait}
import org.jgll.sppf._
import scala.virtualization.lms.common.Base

trait AbstractLookupTableTrait {
  self: GrammarTrait
   with GrammarSlotTrait
   with HeadGrammarSlotTrait
   with NonPackedNodeTrait
   with NonterminalSymbolNodeTrait
   with LookupTableTrait
   with Base
   with ListSymbolNodeTrait
   with IntermediateNodeTrait =>
  abstract class AbstractLookupTable(protected val grammar: Grammar) extends LookupTable {

    protected val slotsSize = grammar.getGrammarSlots.size

    protected def createNonPackedNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Rep[Int]): NonPackedNode = {
      var key: NonPackedNode = null
      if (slot.isInstanceOf[HeadGrammarSlot]) {
        val head = slot.asInstanceOf[HeadGrammarSlot]
        key = if (head.getNonterminal.isEbnfList)
                new ListSymbolNode(slot, leftExtent, rightExtent)
              else
                new NonterminalSymbolNode(slot, leftExtent, rightExtent)
      } else {
        key = new IntermediateNode(slot, leftExtent, rightExtent)
      }
      key
    }
  }
}