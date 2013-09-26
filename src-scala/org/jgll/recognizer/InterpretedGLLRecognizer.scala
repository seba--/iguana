package org.jgll.recognizer

import org.jgll.grammar.slot.GrammarSlotTrait

trait InterpretedGLLRecognizerTrait {
  self: GrammarSlotTrait
   with GSSNodeTrait
   with AbstractGLLRecognizerTrait
   =>
  class InterpretedGLLRecognizer extends AbstractGLLRecognizer {

    override def add(slot: GrammarSlot, u: GSSNode, inputIndex: Int) {
      if (slot == AbstractGLLRecognizer.startSlot && inputIndex == endIndex && u == AbstractGLLRecognizer.u0) {
        descriptorStack.clear()
        recognized = true
        return
      }
      super.add(slot, u, inputIndex)
    }
  }
}