package org.jgll.recognizer

import org.jgll.grammar.slot.GrammarSlot
import org.jgll.parser.HashFunctions
import org.jgll.util.hashing.ExternalHasher
import org.jgll.util.hashing.hashfunction.HashFunction
import Descriptor._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object Descriptor {

  val externalHasher = new DescriptorExternalHasher()

  @SerialVersionUID(1L)
  class DescriptorExternalHasher extends ExternalHasher[Descriptor] {

    override def hash(descriptor: Descriptor, f: HashFunction): Int = {
      f.hash(descriptor.slot.getId, descriptor.inputIndex, descriptor.gssNode.getGrammarSlot.getId, descriptor.gssNode.getInputIndex)
    }

    override def equals(d1: Descriptor, d2: Descriptor): Boolean = {
      d1.inputIndex == d2.getInputIndex && d1.slot == d2.slot && 
        d1.gssNode.getGrammarSlot.getId == d2.gssNode.getGrammarSlot.getId && 
        d1.gssNode.getInputIndex == d2.gssNode.getInputIndex
    }
  }
}

class Descriptor(private val slot: GrammarSlot, private val gssNode: GSSNode, @BeanProperty val inputIndex: Int)
    {

  assert(slot != null)

  assert(gssNode != null)

  assert(inputIndex >= 0)

  def getGrammarSlot(): GrammarSlot = slot

  def getGSSNode(): GSSNode = gssNode

  override def hashCode(): Int = {
    externalHasher.hash(this, HashFunctions.defaulFunction())
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      return true
    }
    if (!(obj.isInstanceOf[Descriptor])) {
      return false
    }
    val other = obj.asInstanceOf[Descriptor]
    inputIndex == other.getInputIndex && slot == other.slot && 
      gssNode.getGrammarSlot.getId == other.gssNode.getGrammarSlot.getId && 
      gssNode.getInputIndex == other.gssNode.getInputIndex
  }

  override def toString(): String = {
    "(" + slot + ", " + inputIndex + ", " + gssNode + ")"
  }
}
