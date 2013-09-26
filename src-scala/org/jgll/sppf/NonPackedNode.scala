package org.jgll.sppf

import org.jgll.grammar.slot.GrammarSlotTrait
import org.jgll.parser.HashFunctions
import org.jgll.util.hashing.ExternalHasher
import org.jgll.util.hashing.hashfunction.HashFunction
import scala.reflect.BeanProperty
import scala.collection.mutable.ListBuffer
import scala.virtualization.lms.common.Base

trait NonPackedNodeTrait {
  self: GrammarSlotTrait
   with SPPFNodeTrait
   with PackedNodeTrait
   with Base
   with DummyNodeTrait =>

  import NonPackedNode._
  object NonPackedNode {

    val externalHasher = new NonPackedNodeExternalHasher()

    val levelBasedExternalHasher = new LevelBasedNonPackedNodeExternalHasher()

    def addChildren(parent: PackedNode, leftChild: SPPFNode, rightChild: SPPFNode) {
      if (leftChild != DummyNode) {
        parent.addChild(leftChild)
      }
      parent.addChild(rightChild)
    }

    @SerialVersionUID(1L)
    class NonPackedNodeExternalHasher extends ExternalHasher[NonPackedNode] {

      override def hash(nonPackedNode: NonPackedNode, f: HashFunction): Int = {
        f.hash(nonPackedNode.slot.getId, nonPackedNode.leftExtent, nonPackedNode.rightExtent)
      }

      override def equals(node1: NonPackedNode, node2: NonPackedNode): Boolean = {
        node1.rightExtent == node2.rightExtent && node1.slot == node2.slot &&
          node1.leftExtent == node2.leftExtent
      }
    }

    @SerialVersionUID(1L)
    class LevelBasedNonPackedNodeExternalHasher extends ExternalHasher[NonPackedNode] {

      override def hash(nonPackedNode: NonPackedNode, f: HashFunction): Int = {
        f.hash(nonPackedNode.slot.getId, nonPackedNode.leftExtent)
      }

      override def equals(node1: NonPackedNode, node2: NonPackedNode): Boolean = {
        node1.slot == node2.slot && node1.leftExtent == node2.leftExtent
      }
    }
  }

  abstract class NonPackedNode(protected val slot: GrammarSlot, protected val leftExtent: Int, protected val rightExtent: Rep[Int])
      extends SPPFNode {

    @BeanProperty
    var firstPackedNodeGrammarSlot: GrammarSlot = null

    @BeanProperty
    var firstPackedNodePivot: Int = _

    protected val children: ListBuffer[SPPFNode] = ListBuffer()

    @BeanProperty
    var countPackedNode: Int = _

    if (slot == null) throw new IllegalArgumentException("Slot cannot be null.")

    if (leftExtent < 0) throw new IllegalArgumentException("leftExtent cannot be negative.")

    if (rightExtent < 0) throw new IllegalArgumentException("rightExtent cannot be negative.")

    if (rightExtent < leftExtent) throw new IllegalArgumentException("rightExtent cannot be less than leftExtent.")

    override def hashCode(): Int = {
      externalHasher.hash(this, HashFunctions.defaulFunction())
    }

    override def equals(obj: Any): Boolean = {
      if (!(obj.isInstanceOf[NonPackedNode])) {
        return false
      }
      val other = obj.asInstanceOf[NonPackedNode]
      rightExtent == other.rightExtent && slot == other.slot &&
        leftExtent == other.leftExtent
    }

    override def getGrammarSlot(): GrammarSlot = slot

    override def getLeftExtent(): Int = leftExtent

    override def getRightExtent(): Rep[Int] = rightExtent

    override def toString(): String = {
      "(%s, %d, %d)".format(getLabel, leftExtent, rightExtent)
    }

    override def getLabel(): String = slot.toString

    def addSecondPackedNode(packedNode: PackedNode, leftChild: SPPFNode, rightChild: SPPFNode): PackedNode = {
      val firstPackedNode = getFirstPackedNode
      for (child <- children) {
        firstPackedNode.addChild(child)
      }
      children.clear()
      children += (firstPackedNode)
      addChildren(packedNode, leftChild, rightChild)
      children += (packedNode)
      countPackedNode = 2
      firstPackedNode
    }

    def addPackedNode(packedNode: PackedNode, leftChild: SPPFNode, rightChild: SPPFNode) {
      addChildren(packedNode, leftChild, rightChild)
      countPackedNode += 1
      children += (packedNode)
    }

    override def isAmbiguous(): Boolean = countPackedNode > 1

    def addChild(node: SPPFNode) {
      if (node.isInstanceOf[PackedNode]) {
        countPackedNode += 1
      }
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

    def getFirstPackedNode(): PackedNode = {
      if (isAmbiguous) {
        return children(0).asInstanceOf[PackedNode]
      }
      new PackedNode(firstPackedNodeGrammarSlot, firstPackedNodePivot, this)
    }

    override def getChildAt(index: Int): SPPFNode = {
      if (children.size > index) {
        return children(index)
      }
      null
    }

    def addFirstPackedNode(slot: GrammarSlot, pivot: Int) {
      this.firstPackedNodeGrammarSlot = slot
      this.firstPackedNodePivot = pivot
      countPackedNode = 1
    }

    override def getChildren(): ListBuffer[SPPFNode] = children

    override def childrenCount(): Int = children.size

    override def getLevel(): Rep[Int] = rightExtent
  }
}