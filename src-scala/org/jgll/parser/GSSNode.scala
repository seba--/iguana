package org.jgll.parser

import java.util.ArrayList
import java.util.List
import org.jgll.grammar.slot.GrammarSlot
import org.jgll.grammar.slot.L0
import org.jgll.util.hashing.ExternalHasher
import org.jgll.util.hashing.Level
import org.jgll.util.hashing.hashfunction.HashFunction
import GSSNode._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object GSSNode {

  val externalHasher = new GSSNodeExternalHasher()

  val levelBasedExternalHasher = new LevelBasedGSSNodeExternalHasher()

  val U0 = new GSSNode(L0.getInstance, 0)

  @SerialVersionUID(1L)
  class GSSNodeExternalHasher extends ExternalHasher[GSSNode] {

    override def hash(node: GSSNode, f: HashFunction): Int = {
      f.hash(node.slot.getId, node.inputIndex)
    }

    override def equals(g1: GSSNode, g2: GSSNode): Boolean = {
      g1.slot.getId == g2.slot.getId && g1.inputIndex == g2.inputIndex
    }
  }

  @SerialVersionUID(1L)
  class LevelBasedGSSNodeExternalHasher extends ExternalHasher[GSSNode] {

    override def hash(node: GSSNode, f: HashFunction): Int = f.hash(node.slot.getId)

    override def equals(g1: GSSNode, g2: GSSNode): Boolean = g1.slot == g2.slot
  }
}

class GSSNode(private val slot: GrammarSlot, @BeanProperty val inputIndex: Int)
    extends Level {

  private var gssEdges: List[GSSEdge] = new ArrayList()

  def addGSSEdge(edge: GSSEdge) {
    gssEdges.add(edge)
  }

  def getEdges(): java.lang.Iterable[GSSEdge] = gssEdges

  def getCountEdges(): Int = gssEdges.size

  def getGrammarSlot(): GrammarSlot = slot

  override def equals(obj: Any): Boolean = {
    if (!(obj.isInstanceOf[GSSNode])) {
      return false
    }
    val other = obj.asInstanceOf[GSSNode]
    slot == other.slot && inputIndex == other.inputIndex
  }

  override def hashCode(): Int = {
    externalHasher.hash(this, HashFunctions.defaulFunction())
  }

  override def toString(): String = "(" + slot + "," + inputIndex + ")"

  override def getLevel(): Int = inputIndex
}
