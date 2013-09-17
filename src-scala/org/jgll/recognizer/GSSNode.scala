package org.jgll.recognizer

import org.jgll.grammar.slot.GrammarSlot
import org.jgll.grammar.slot.L0
import org.jgll.parser.HashFunctions
import org.jgll.util.hashing.CuckooHashSet
import org.jgll.util.hashing.ExternalHasher
import org.jgll.util.hashing.IntegerExternalHasher
import org.jgll.util.hashing.hashfunction.HashFunction
import GSSNode._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object GSSNode {

  val externalHasher = new GSSNodeExternalHasher()

  val U0 = new GSSNode(L0.getInstance, 0)

  @SerialVersionUID(1L)
  class GSSNodeExternalHasher extends ExternalHasher[GSSNode] {

    override def hash(node: GSSNode, f: HashFunction): Int = {
      f.hash(node.getGrammarSlot.getId, node.getInputIndex)
    }

    override def equals(g1: GSSNode, g2: GSSNode): Boolean = {
      g1.slot == g2.slot && g1.inputIndex == g2.inputIndex
    }
  }
}

class GSSNode(private val slot: GrammarSlot, @BeanProperty val inputIndex: Int)
    {

  @BeanProperty
  val children = new CuckooHashSet(new GSSNodeExternalHasher())

  @BeanProperty
  val poppedIndices = new CuckooHashSet[Integer](IntegerExternalHasher.getInstance)

  def hasChild(child: GSSNode): Boolean = children.contains(child)

  def addChild(edge: GSSNode) {
    children.add(edge)
  }

  def addPoppedIndex(i: Int) {
    poppedIndices.add(i)
  }

  def getGrammarSlot(): GrammarSlot = slot

  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[GSSNode])) {
      return false
    }
    val other = obj.asInstanceOf[GSSNode]
    other.slot == slot && other.inputIndex == inputIndex
  }

  override def hashCode(): Int = {
    externalHasher.hash(this, HashFunctions.defaulFunction())
  }

  override def toString(): String = slot + "," + inputIndex
}
