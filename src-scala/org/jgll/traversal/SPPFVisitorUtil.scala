package org.jgll.traversal

import org.jgll.sppf._
import scala.collection.mutable.ListBuffer

trait SPPFVisitorUtilTrait {
  self: SPPFNodeTrait
   with SPPFVisitorTrait
   with NonterminalSymbolNodeTrait
   with IntermediateNodeTrait
   with PackedNodeTrait
   with NonPackedNodeTrait
   with ListSymbolNodeTrait
  =>
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
          val restOfChildren = ListBuffer[SPPFNode]()
          node.removeChild(intermediateNode)
          while (node.childrenCount() > 0) {
            restOfChildren += (node.getChildAt(0))
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
          val restOfChildren = ListBuffer[SPPFNode]()
          parentOfPackedNode.removeChild(parent)
          parent.removeChild(intermediateNode)
          for (sn <- parent.getChildren) {
            restOfChildren += (sn)
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
}