package org.jgll_staged.traversal

import java.util.ArrayList
import java.util.Iterator
import java.util.List
import org.jgll_staged.grammar.java.lang.Character
import org.jgll_staged.grammar.CharacterClass
import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.grammar.slot.LastGrammarSlot
import org.jgll_staged.grammar.slot.TerminalGrammarSlot
import org.jgll_staged.sppf.IntermediateNode
import org.jgll_staged.sppf.ListSymbolNode
import org.jgll_staged.sppf.NonterminalSymbolNode
import org.jgll_staged.sppf.PackedNode
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.sppf.TerminalSymbolNode
import org.jgll_staged.util.Input
import org.jgll_staged.traversal.SPPFVisitorUtil._
//remove if not needed
import scala.collection.JavaConversions._

class ModelBuilderVisitor[T, U](private var input: Input, private var listener: NodeListener[T, U])
    extends SPPFVisitor {

  override def visit(terminal: TerminalSymbolNode) {
    if (!terminal.isVisited) {
      terminal.setVisited(true)
      if (terminal.getMatchedChar == TerminalSymbolNode.EPSILON) {
        terminal.setObject(Result.skip())
      } else {
        val result = listener.terminal(terminal.getMatchedChar, input.getPositionInfo(terminal.getLeftExtent, 
          terminal.getRightExtent))
        terminal.setObject(result)
      }
    }
  }

  override def visit(nonterminalSymbolNode: NonterminalSymbolNode) {
    removeIntermediateNode(nonterminalSymbolNode)
    if (!nonterminalSymbolNode.isVisited) {
      nonterminalSymbolNode.setVisited(true)
      if (nonterminalSymbolNode.isAmbiguous) {
        buildAmbiguityNode(nonterminalSymbolNode)
      } else if (nonterminalSymbolNode.isKeywordNode) {
        val head = nonterminalSymbolNode.getGrammarSlot.asInstanceOf[HeadGrammarSlot]
        var currentSlot = head.getAlternateAt(0).getFirstSlot
        val list = new ArrayList[U]()
        val i = nonterminalSymbolNode.getLeftExtent
        while (!(currentSlot.isInstanceOf[LastGrammarSlot])) {
          val terminal = currentSlot.asInstanceOf[TerminalGrammarSlot].getTerminal.asInstanceOf[CharacterClass]
          assert(terminal.getRanges.size == 1)
          val result = listener.terminal(terminal.getRanges.get(0).getStart, input.getPositionInfo(i, 
            i))
          list.add(result.getObject)
          currentSlot = currentSlot.next()
        }
        val slot = nonterminalSymbolNode.getFirstPackedNodeGrammarSlot.asInstanceOf[LastGrammarSlot]
        listener.startNode(slot.getObject.asInstanceOf[T])
        val result = listener.endNode(slot.getObject.asInstanceOf[T], list, input.getPositionInfo(nonterminalSymbolNode.getLeftExtent, 
          nonterminalSymbolNode.getRightExtent))
        nonterminalSymbolNode.setObject(result)
      } else {
        val slot = nonterminalSymbolNode.getFirstPackedNodeGrammarSlot.asInstanceOf[LastGrammarSlot]
        listener.startNode(slot.getObject.asInstanceOf[T])
        visitChildren(nonterminalSymbolNode, this)
        val result = listener.endNode(slot.getObject.asInstanceOf[T], getChildrenValues(nonterminalSymbolNode), 
          input.getPositionInfo(nonterminalSymbolNode.getLeftExtent, nonterminalSymbolNode.getRightExtent))
        nonterminalSymbolNode.setObject(result)
      }
    }
  }

  private def buildAmbiguityNode(nonterminalSymbolNode: NonterminalSymbolNode) {
    var nPackedNodes = 0
    for (child <- nonterminalSymbolNode.getChildren) {
      val packedNode = child.asInstanceOf[PackedNode]
      val slot = packedNode.getGrammarSlot.asInstanceOf[LastGrammarSlot]
      listener.startNode(slot.getObject.asInstanceOf[T])
      packedNode.accept(this)
      val result = listener.endNode(slot.getObject.asInstanceOf[T], getChildrenValues(packedNode), input.getPositionInfo(packedNode.getLeftExtent, 
        packedNode.getRightExtent))
      packedNode.setObject(result)
      if (result != Result.filter()) {
        nPackedNodes += 1
      }
    }
    if (nPackedNodes > 1) {
      val result = listener.buildAmbiguityNode(getChildrenValues(nonterminalSymbolNode), input.getPositionInfo(nonterminalSymbolNode.getLeftExtent, 
        nonterminalSymbolNode.getRightExtent))
      nonterminalSymbolNode.setObject(result)
    }
  }

  override def visit(node: IntermediateNode) {
    throw new RuntimeException("Should not be here!")
  }

  override def visit(packedNode: PackedNode) {
    removeIntermediateNode(packedNode)
    removeListSymbolNode(packedNode)
    if (!packedNode.isVisited) {
      packedNode.setVisited(true)
      visitChildren(packedNode, this)
    }
  }

  override def visit(listNode: ListSymbolNode) {
    removeListSymbolNode(listNode)
    if (!listNode.isVisited) {
      listNode.setVisited(true)
      if (listNode.isAmbiguous) {
        buildAmbiguityNode(listNode)
      } else {
        val slot = listNode.getFirstPackedNodeGrammarSlot.asInstanceOf[LastGrammarSlot]
        listener.startNode(slot.getObject.asInstanceOf[T])
        visitChildren(listNode, this)
        val result = listener.endNode(slot.getObject.asInstanceOf[T], getChildrenValues(listNode), input.getPositionInfo(listNode.getLeftExtent, 
          listNode.getRightExtent))
        listNode.setObject(result)
      }
    }
  }

  private def getChildrenValues(node: SPPFNode): java.lang.Iterable[U] = {
    val iterator = node.getChildren.iterator()
    new java.lang.Iterable[U]() {

      override def iterator(): Iterator[U] = {
        new Iterator[U]() {

          private var next: SPPFNode = _

          override def hasNext(): Boolean = {
            while (iterator.hasNext) {
              next = iterator.next()
              if (next.getObject == Result.filter()) {
                node.setObject(Result.filter())
                if (!iterator.hasNext) {
                  return false
                }
                next = iterator.next()
              } else if (next.getObject == Result.skip()) {
                if (!iterator.hasNext) {
                  return false
                }
                next = iterator.next()
              } else if (next.getObject == null) {
                if (!iterator.hasNext) {
                  return false
                }
                next = iterator.next()
              } else {
                return true
              }
            }
            false
          }

          override def next(): U = {
            next.getObject.asInstanceOf[Result[U]].getObject
          }

          override def remove() {
            throw new UnsupportedOperationException()
          }
        }
      }
    }
  }
}