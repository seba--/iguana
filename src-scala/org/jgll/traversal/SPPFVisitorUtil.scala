package org.jgll.traversal

import java.util.ArrayList
import java.util.List
import org.jgll.sppf.IntermediateNode
import org.jgll.sppf.ListSymbolNode
import org.jgll.sppf.NonPackedNode
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.PackedNode
import org.jgll.sppf.SPPFNode
//remove if not needed
import scala.collection.JavaConversions._

object SPPFVisitorUtil {

  def visitChildren(node: SPPFNode, visitor: SPPFVisitor) {
    for (child <- node.getChildren) {
      child.accept(visitor)
    }
  }

  def removeIntermediateNode(node: NonterminalSymbolNode) {
    if (node.getChildAt(0).isInstanceOf[IntermediateNode]) {
      val intermediateNode = node.getChildAt(0).asInstanceOf[IntermediateNode]
      if (intermediateNode.isAmbiguous) {
        val restOfChildren = new ArrayList[SPPFNode]()
        node.removeChild(intermediateNode)
        while (node.childrenCount() > 0) {
          restOfChildren.add(node.getChildAt(0))
          node.removeChild(node.getChildAt(0))
        }
        for (child <- intermediateNode.getChildren) {
          val pn = child.asInstanceOf[PackedNode]
          val newPackedNode = new PackedNode(node.getFirstPackedNodeGrammarSlot, node.childrenCount(), 
            node)
          for (sn <- pn.getChildren) {
            newPackedNode.addChild(sn)
          }
          for (c <- restOfChildren) {
            newPackedNode.addChild(c)
          }
          node.addChild(newPackedNode)
          removeIntermediateNode(newPackedNode)
        }
      } else {
        node.replaceWithChildren(intermediateNode)
        removeIntermediateNode(node)
      }
    }
  }

  def removeIntermediateNode(parent: PackedNode) {
    if (parent.getChildAt(0).isInstanceOf[IntermediateNode]) {
      val intermediateNode = parent.getChildAt(0).asInstanceOf[IntermediateNode]
      if (intermediateNode.isAmbiguous) {
        val parentOfPackedNode = parent.getParent.asInstanceOf[NonPackedNode]
        val restOfChildren = new ArrayList[SPPFNode]()
        parentOfPackedNode.removeChild(parent)
        parent.removeChild(intermediateNode)
        for (sn <- parent.getChildren) {
          restOfChildren.add(sn)
        }
        for (child <- intermediateNode.getChildren) {
          val pn = child.asInstanceOf[PackedNode]
          val newPackedNode = new PackedNode(parentOfPackedNode.getFirstPackedNodeGrammarSlot, parentOfPackedNode.childrenCount(), 
            parentOfPackedNode)
          for (sn <- pn.getChildren) {
            newPackedNode.addChild(sn)
          }
          for (c <- restOfChildren) {
            newPackedNode.addChild(c)
          }
          parentOfPackedNode.addChild(newPackedNode)
          removeIntermediateNode(newPackedNode)
        }
      } else {
        parent.replaceWithChildren(intermediateNode)
        removeIntermediateNode(parent)
      }
    }
  }

  def removeListSymbolNode(node: ListSymbolNode) {
    if (!node.isAmbiguous) {
      removeIntermediateNode(node)
      if (node.getChildAt(0).isInstanceOf[ListSymbolNode]) {
        val child = node.getChildAt(0).asInstanceOf[ListSymbolNode]
        node.replaceWithChildren(child)
        removeListSymbolNode(node)
      }
    }
  }

  def removeListSymbolNode(node: PackedNode) {
    removeIntermediateNode(node)
    if (node.getChildAt(0).isInstanceOf[ListSymbolNode]) {
      val child = node.getChildAt(0).asInstanceOf[ListSymbolNode]
      node.replaceWithChildren(child)
      removeListSymbolNode(node)
    }
  }
}
