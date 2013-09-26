package org.jgll.sppf

import org.jgll.grammar.slot.GrammarSlotTrait
import org.jgll.parser.HashFunctions
import org.jgll.traversal.SPPFVisitorTrait
import org.jgll.util.hashing.ExternalHasher
import org.jgll.util.hashing.hashfunction.HashFunction
import scala.reflect.BeanProperty
import scala.collection.mutable.ListBuffer

trait PackedNodeTrait {
  self: GrammarSlotTrait
   with SPPFVisitorTrait
   with SPPFNodeTrait
   with NonPackedNodeTrait =>

  import PackedNode._
  object PackedNode {

    val externalHasher = new PackedNodeExternalHasher()

    val levelBasedExternalHasher = new PackedNodeExternalHasher()

    @SerialVersionUID(1L)
    class PackedNodeExternalHasher extends ExternalHasher[PackedNode] {

      override def hash(packedNode: PackedNode, f: HashFunction): Int = {
        f.hash(packedNode.slot.getId, packedNode.pivot, packedNode.parent.getGrammarSlot.getId, packedNode.parent.getLeftExtent,
          packedNode.parent.getRightExtent)
      }

      override def equals(node1: PackedNode, node2: PackedNode): Boolean = {
        node1.slot == node2.slot && node1.pivot == node2.pivot &&
          node1.parent.getGrammarSlot == node2.parent.getGrammarSlot &&
          node1.parent.getLeftExtent == node2.parent.getLeftExtent &&
          node1.parent.getRightExtent == node2.parent.getRightExtent
      }
    }

    @SerialVersionUID(1L)
    class PackedNodeLevelBasedExternalHasher extends ExternalHasher[PackedNode] {

      override def hash(packedNode: PackedNode, f: HashFunction): Int = {
        f.hash(packedNode.slot.getId, packedNode.pivot, packedNode.parent.getGrammarSlot.getId, packedNode.parent.getLeftExtent)
      }

      override def equals(node1: PackedNode, node2: PackedNode): Boolean = {
        node1.slot == node2.slot && node1.pivot == node2.pivot &&
          node1.parent.getGrammarSlot == node2.parent.getGrammarSlot &&
          node1.parent.getLeftExtent == node2.parent.getLeftExtent
      }
    }
  }

  class PackedNode(private val slot: GrammarSlot, @BeanProperty val pivot: Int, @BeanProperty val parent: NonPackedNode)
      extends SPPFNode {

    @BeanProperty
    val children = ListBuffer[SPPFNode](2)

    if (slot == null) {
      throw new IllegalArgumentException("Gramar slot cannot be null.")
    }

    if (pivot < 0) {
      throw new IllegalArgumentException("Pivot should be a positive integer number.")
    }

    if (parent == null) {
      throw new IllegalArgumentException("The parent node cannot be null.")
    }

    override def equals(obj: Any): Boolean = {
      if (!(obj.isInstanceOf[PackedNode])) {
        return false
      }
      val other = obj.asInstanceOf[PackedNode]
      externalHasher.equals(this, other)
    }

    override def getGrammarSlot(): GrammarSlot = slot

    def addChild(node: SPPFNode) {
      children += (node)
    }

    def removeChild(node: SPPFNode) {
      children -= (node)
    }

    def replaceWithChildren(node: SPPFNode) {
      var index = children.indexOf(node)
      children -= (node)
      if (index >= 0) {
        for (child <- node.getChildren) {
          children.insert(index, child)
          index += 1
        }
      }
    }

    override def hashCode(): Int = {
      externalHasher.hash(this, HashFunctions.defaulFunction())
    }

    override def toString(): String = {
      "(%s, %d)" format(getLabel, getPivot)
    }

    override def getLabel(): String = slot.toString

    override def getLeftExtent(): Int = parent.getLeftExtent

    override def getRightExtent(): Int = parent.getRightExtent

    override def accept(visitAction: SPPFVisitor) {
      visitAction.visit(this)
    }

    override def getChildAt(index: Int): SPPFNode = {
      if (children.size > index) {
        return children(index)
      }
      null
    }

    override def childrenCount(): Int = children.size

    override def isAmbiguous(): Boolean = false

    override def getLevel(): Int = parent.getRightExtent
  }
}
