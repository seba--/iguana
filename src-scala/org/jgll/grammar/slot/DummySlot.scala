package org.jgll.grammar.slot

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class DummySlot extends LastGrammarSlot(0, null, null, null) {

  override def toString(): String = "Dummy"
}
