package org.jgll.traversal

import java.util.ArrayList
import java.util.Iterator
import java.util.List
import org.jgll.grammar.Character
import org.jgll.grammar.CharacterClass
import org.jgll.grammar.slot.BodyGrammarSlot
import org.jgll.grammar.slot.HeadGrammarSlot
import org.jgll.grammar.slot.LastGrammarSlot
import org.jgll.grammar.slot.TerminalGrammarSlot
import org.jgll.sppf.IntermediateNode
import org.jgll.sppf.ListSymbolNode
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.PackedNode
import org.jgll.sppf.SPPFNode
import org.jgll.sppf.TerminalSymbolNode
import org.jgll.traversal.SPPFVisitorUtil._
import org.jgll.util.InputTrait

//remove if not needed
import scala.collection.JavaConversions._

trait ModelBuilderVisitorTrait extends InputTrait {
  class ModelBuilderVisitor[T, U](private var input: Input, private var listener: NodeListener[T, U])
      extends SPPFVisitor {

    override def visit(terminal: TerminalSymbolNode) {
      if (!terminal.isVisited) {
        terminal.setVisited(true)
        if (terminal.getMatchedChar == TerminalSymbolNode.EPSILON) {
          terminal.setObj(Result.skip())
        } else {
          val result = listener.terminal(terminal.getMatchedChar, input.getPositionInfo(terminal.getLeftExtent,
            terminal.getRightExtent))
          terminal.setObj(result)
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
            val terminal: CharacterClass = currentSlot.asInstanceOf[TerminalGrammarSlot].getTerminal.asInstanceOf[CharacterClass]
            assert(terminal.ranges.size == 1)
            val result = listener.terminal(terminal.ranges.get(0).getStart, input.getPositionInfo(i,
              i))
            list.add(result.getObj)
            currentSlot = currentSlot.next()
          }
          val slot = nonterminalSymbolNode.getFirstPackedNodeGrammarSlot.asInstanceOf[LastGrammarSlot]
          listener.startNode(slot.getObj.asInstanceOf[T])
          val result = listener.endNode(slot.getObj.asInstanceOf[T], list, input.getPositionInfo(nonterminalSymbolNode.getLeftExtent,
            nonterminalSymbolNode.getRightExtent))
          nonterminalSymbolNode.setObj(result)
        } else {
          val slot = nonterminalSymbolNode.getFirstPackedNodeGrammarSlot.asInstanceOf[LastGrammarSlot]
          listener.startNode(slot.getObj.asInstanceOf[T])
          visitChildren(nonterminalSymbolNode, this)
          val result = listener.endNode(slot.getObj.asInstanceOf[T], getChildrenValues(nonterminalSymbolNode),
            input.getPositionInfo(nonterminalSymbolNode.getLeftExtent, nonterminalSymbolNode.getRightExtent))
          nonterminalSymbolNode.setObj(result)
        }
      }
    }

    private def buildAmbiguityNode(nonterminalSymbolNode: NonterminalSymbolNode) {
      var nPackedNodes = 0
      for (child <- nonterminalSymbolNode.getChildren) {
        val packedNode = child.asInstanceOf[PackedNode]
        val slot = packedNode.getGrammarSlot.asInstanceOf[LastGrammarSlot]
        listener.startNode(slot.getObj.asInstanceOf[T])
        packedNode.accept(this)
        val result = listener.endNode(slot.getObj.asInstanceOf[T], getChildrenValues(packedNode), input.getPositionInfo(packedNode.getLeftExtent,
          packedNode.getRightExtent))
        packedNode.setObj(result)
        if (result != Result.filter()) {
          nPackedNodes += 1
        }
      }
      if (nPackedNodes > 1) {
        val result = listener.buildAmbiguityNode(getChildrenValues(nonterminalSymbolNode), input.getPositionInfo(nonterminalSymbolNode.getLeftExtent,
          nonterminalSymbolNode.getRightExtent))
        nonterminalSymbolNode.setObj(result)
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
          listener.startNode(slot.getObj.asInstanceOf[T])
          visitChildren(listNode, this)
          val result = listener.endNode(slot.getObj.asInstanceOf[T], getChildrenValues(listNode), input.getPositionInfo(listNode.getLeftExtent,
            listNode.getRightExtent))
          listNode.setObj(result)
        }
      }
    }

    private def getChildrenValues(node: SPPFNode): java.lang.Iterable[U] = {
      val _iterator = node.getChildren.iterator()
      new java.lang.Iterable[U]() {

        override def iterator(): Iterator[U] = {
          new Iterator[U]() {

            private var _next: SPPFNode = _

            override def hasNext(): Boolean = {
              while (_iterator.hasNext) {
                _next = _iterator.next()
                if (_next.getObj == Result.filter()) {
                  node.setObj(Result.filter())
                  if (!_iterator.hasNext) {
                    return false
                  }
                  _next = _iterator.next()
                } else if (_next.getObj == Result.skip()) {
                  if (!_iterator.hasNext) {
                    return false
                  }
                  _next = _iterator.next()
                } else if (_next.getObj == null) {
                  if (!_iterator.hasNext) {
                    return false
                  }
                  _next = _iterator.next()
                } else {
                  return true
                }
              }
              false
            }

            override def next(): U = {
              _next.getObj.asInstanceOf[Result[U]].getObj
            }

            override def remove() {
              throw new UnsupportedOperationException()
            }
          }
        }
      }
    }
  }
}