package org.jgll.recognizer

import org.jgll.grammar.Grammar
import org.jgll.grammar.slot.BodyGrammarSlot
import org.jgll.grammar.slot.GrammarSlot
import org.jgll.util.Input
//remove if not needed
import scala.collection.JavaConversions._

trait GLLRecognizer {

  def recognize(input: Input, grammar: Grammar, nonterminalName: String): Boolean

  def recognize(input: Input, 
      start: Int, 
      end: Int, 
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
