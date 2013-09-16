package org.jgll_staged.recognizer

import org.jgll_staged.grammar.slot.GrammarSlot
//remove if not needed
import scala.collection.JavaConversions._

class InterpretedGLLRecognizer extends AbstractGLLRecognizer {

  override def add(slot: GrammarSlot, u: GSSNode, inputIndex: Int) {
    if (slot == startSlot && inputIndex == endIndex && u == u0) {
      descriptorStack.clear()
      recognized = true
      return
    }
    super.add(slot, u, inputIndex)
  }
}