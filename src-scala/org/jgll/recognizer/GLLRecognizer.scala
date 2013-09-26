package org.jgll.recognizer

import org.jgll.util.InputTrait
import org.jgll.grammar.GrammarTrait
import org.jgll.grammar.slot.BodyGrammarSlotTrait
import org.jgll.grammar.slot.GrammarSlotTrait


trait GLLRecognizerTrait {
  self: GrammarTrait
   with BodyGrammarSlotTrait
   with GrammarSlotTrait
   with InputTrait
   with GSSNodeTrait
   with DescriptorTrait
  =>
  trait GLLRecognizer {

    def recognize(input: Input, grammar: Grammar, nonterminalName: String): Boolean

    def recognize(input: Input,
        start: Int,
        end: Rep[Int],
        slot: BodyGrammarSlot): Boolean

    def add(label: GrammarSlot, u: GSSNode, inputIndex: Int): Unit

    def pop(u: GSSNode, i: Int): Unit

    def create(L: GrammarSlot, u: GSSNode, i: Int): GSSNode

    def hasNextDescriptor(): Boolean

    def nextDescriptor(): Descriptor

    def getCi(): Int

    def getCu(): GSSNode

    def update(gssNode: GSSNode, inputIndex: Int): Unit

    def recognitionError(gssNode: GSSNode, inputIndex: Int): Unit
  }
}