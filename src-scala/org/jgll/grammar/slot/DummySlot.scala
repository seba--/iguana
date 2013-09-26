package org.jgll.grammar.slot

trait DummySlotTrait { self: LastGrammarSlotTrait =>
  @SerialVersionUID(1L)
  class DummySlot extends LastGrammarSlot(0, null, null, null) {

    override def toString(): String = "Dummy"
  }
}