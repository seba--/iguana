package org.jgll_staged.parser

import java.io.PrintStream
import java.util.ArrayDeque
import java.util.Deque
import java.util.HashSet
import java.util.Set
import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.grammar.slot.GrammarSlot
import org.jgll_staged.util.Input
import ParseError._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object ParseError {

  def getMessage(input: Input, inputIndex: Int): String = {
    val lineNumber = input.getLineNumber(inputIndex)
    val columnNumber = input.getColumnNumber(inputIndex)
    "Parse error at line:" + lineNumber + " column:" + columnNumber
  }
}

class ParseError(@BeanProperty val slot: GrammarSlot, 
    input: Input, 
    @BeanProperty val inputIndex: Int, 
    curerntNode: GSSNode) extends Exception(getMessage(input, inputIndex)) {

  private val currentNode = curerntNode

  def printGrammarTrace() {
    printGrammarTrace(System.out)
  }

  def printGrammarTrace(out: PrintStream) {
    out.println(toString)
    indent(out, 1, new GSSNode(slot.asInstanceOf[BodyGrammarSlot].next(), inputIndex))
    var gssNode = currentNode
    while (gssNode != GSSNode.U0) {
      indent(out, 1, gssNode)
      gssNode = findMergePoint(gssNode, out, 1)
    }
  }

  private def indent(out: PrintStream, i: Int, node: GSSNode) {
    if (node == GSSNode.U0) {
      return
    }
    out.println(String.format("%" + i * 2 + "s, %d", node.getGrammarSlot.asInstanceOf[BodyGrammarSlot].previous()
      .toString, node.getInputIndex))
  }

  private def findMergePoint(node: GSSNode, out: PrintStream, i: Int): GSSNode = {
    if (node.getCountEdges == 1) {
      return node.getEdges.iterator().next().getDestination
    }
    reachableFrom(node, out, i)
  }

  private def reachableFrom(node: GSSNode, out: PrintStream, i: Int): GSSNode = {
    val set = new HashSet[GSSNode]()
    val frontier = new ArrayDeque[GSSNode]()
    for (edge <- node.getEdges) {
      val destination = edge.getDestination
      set.add(destination)
      frontier.add(destination)
      indent(out, i + 1, destination)
    }
    i += 1
    while (frontier.size > 1) {
      val f = frontier.poll().asInstanceOf[GSSNode]
      for (edge <- f.getEdges) {
        val destination = edge.getDestination
        if (!set.contains(destination)) {
          set.add(destination)
          frontier.add(destination)
          indent(out, i + 1, destination)
        }
      }
    }
    frontier.poll()
  }
}
