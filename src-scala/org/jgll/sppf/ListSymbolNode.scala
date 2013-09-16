package org.jgll.sppf

import org.jgll.grammar.slot.GrammarSlot
import org.jgll.traversal.SPPFVisitor
//remove if not needed
import scala.collection.JavaConversions._

class ListSymbolNode(slot: GrammarSlot, leftExtent: Int, rightExtent: Int) extends NonterminalSymbolNode(slot, 
  leftExtent, rightExtent) {

  override def accept(visitAction: SPPFVisitor) {
    visitAction.visit(this)
  }
}
