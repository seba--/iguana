package org.jgll.recognizer

import org.jgll.grammar.slot.GrammarSlot
//remove if not needed
import scala.collection.JavaConversions._

class PrefixGLLRecognizer extends AbstractGLLRecognizer {

  override def add(slot: GrammarSlot, u: GSSNode, inputIndex: Int) {
    if (slot == AbstractGLLRecognizer.startSlot && u == AbstractGLLRecognizer.u0) {
      descriptorStack.clear()
      recognized = true
      return
    }
    super.add(slot, u, inputIndex)
  }
}
