package org.jgll_staged.parser

import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.util.hashing.ExternalHasher
import org.jgll_staged.util.hashing.Level
import org.jgll_staged.util.hashing.hashfunction.HashFunction
import Descriptor._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object Descriptor {

  val externalHasher = new DescriptorExternalHasher()

  val levelBasedExternalHasher = new LevelBasedExternalHasher()

  @SerialVersionUID(1L)
  class DescriptorExternalHasher extends ExternalHasher[Descriptor] {

    override def hash(d: Descriptor, f: HashFunction): Int = {
      f.hash(d.slot.getId, d.sppfNode.getGrammarSlot.getId, d.gssNode.getGrammarSlot.getId, d.gssNode.getInputIndex, 
        d.inputIndex)
    }

    override def equals(d1: Descriptor, d2: Descriptor): Boolean = {
      d1.inputIndex == d2.inputIndex && d1.slot.getId == d2.slot.getId && 
        d1.sppfNode.getGrammarSlot == d2.sppfNode.getGrammarSlot && 
        d1.gssNode.getGrammarSlot == d2.gssNode.getGrammarSlot && 
        d1.gssNode.getInputIndex == d2.gssNode.getInputIndex
    }
  }

  @SerialVersionUID(1L)
  class LevelBasedExternalHasher extends ExternalHasher[Descriptor] {

    override def hash(d: Descriptor, f: HashFunction): Int = {
      f.hash(d.slot.getId, d.sppfNode.getGrammarSlot.getId, d.gssNode.getGrammarSlot.getId, d.gssNode.getInputIndex)
    }

    override def equals(d1: Descriptor, d2: Descriptor): Boolean = {
      d1.slot.getId == d2.slot.getId && 
        d1.sppfNode.getGrammarSlot == d2.sppfNode.getGrammarSlot && 
        d1.gssNode.getGrammarSlot == d2.gssNode.getGrammarSlot && 
        d1.gssNode.getInputIndex == d2.gssNode.getInputIndex
    }
  }
}

class Descriptor(private val slot: GrammarSlot, 
    private val gssNode: GSSNode, 
    @BeanProperty val inputIndex: Int, 
    private val sppfNode: SPPFNode) extends Level {

  assert(slot != null)

  assert(gssNode != null)

  assert(inputIndex >= 0)

  assert(sppfNode != null)

  def getGrammarSlot(): GrammarSlot = slot

  def getGSSNode(): GSSNode = gssNode

  def getSPPFNode(): SPPFNode = sppfNode

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
    slot == other.slot && 
      sppfNode.getGrammarSlot == other.sppfNode.getGrammarSlot && 
      gssNode.getGrammarSlot == other.gssNode.getGrammarSlot && 
      gssNode.getInputIndex == other.gssNode.getInputIndex && 
      inputIndex == other.getInputIndex
  }

  override def toString(): String = {
    "(" + slot + ", " + inputIndex + ", " + gssNode.getGrammarSlot + 
      ", " + 
      sppfNode + 
      ")"
  }

  override def getLevel(): Int = inputIndex
}
