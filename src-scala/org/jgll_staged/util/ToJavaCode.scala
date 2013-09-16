package org.jgll_staged.util

import org.jgll_staged.grammar.Grammar
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.sppf.IntermediateNode
import org.jgll_staged.sppf.ListSymbolNode
import org.jgll_staged.sppf.NonterminalSymbolNode
import org.jgll_staged.sppf.PackedNode
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.sppf.TerminalSymbolNode
import org.jgll_staged.traversal.SPPFVisitor
import ToJavaCode._
//remove if not needed
import scala.collection.JavaConversions._

object ToJavaCode {

  def toJavaCode(node: NonterminalSymbolNode, grammar: Grammar): String = {
    val toJavaCode = new ToJavaCode(grammar)
    toJavaCode.visit(node)
    toJavaCode.toString
  }
}

class ToJavaCode(private var grammar: Grammar) extends SPPFVisitor {

  private var count: Int = 1

  private var sb: StringBuilder = new StringBuilder()

  override def visit(node: TerminalSymbolNode) {
    if (!node.isVisited) {
      node.setVisited(true)
      sb.append("TerminalSymbolNode node" + count + " = new TerminalSymbolNode(" + 
        node.getMatchedChar + 
        ", " + 
        node.getLeftExtent + 
        ");\n")
      node.setObj("node" + count)
      count += 1
    }
  }

  override def visit(node: NonterminalSymbolNode) {
    if (!node.isVisited) {
      node.setVisited(true)
      node.setObj("node" + count)
      if (grammar.isNewNonterminal(node.getGrammarSlot.asInstanceOf[HeadGrammarSlot])) {
        val index = grammar.getIndex(node.getGrammarSlot.asInstanceOf[HeadGrammarSlot])
        sb.append("NonterminalSymbolNode node" + count + " = new NonterminalSymbolNode(" + 
          "grammar.getNonterminalByNameAndIndex(\"" + 
          node.getGrammarSlot + 
          "\", " + 
          index + 
          "), " + 
          node.getLeftExtent + 
          ", " + 
          node.getRightExtent + 
          ");\n")
      } else {
        sb.append("NonterminalSymbolNode node" + count + " = new NonterminalSymbolNode(" + 
          "grammar.getNonterminalByName(\"" + 
          node.getGrammarSlot + 
          "\"), " + 
          node.getLeftExtent + 
          ", " + 
          node.getRightExtent + 
          ");\n")
      }
      count += 1
      visitChildren(node)
      addChildren(node)
    }
  }

  override def visit(node: IntermediateNode) {
    if (!node.isVisited) {
      node.setVisited(true)
      node.setObj("node" + count)
      sb.append("IntermediateNode node" + count + " = new IntermediateNode(" + 
        "grammar.getGrammarSlotByName(\"" + 
        node.getGrammarSlot + 
        "\"), " + 
        node.getLeftExtent + 
        ", " + 
        node.getRightExtent + 
        ");\n")
      count += 1
      visitChildren(node)
      addChildren(node)
    }
  }

  override def visit(node: PackedNode) {
    if (!node.isVisited) {
      node.setVisited(true)
      node.setObj("node" + count)
      sb.append("PackedNode node" + count + " = new PackedNode(" + "grammar.getGrammarSlotByName(\"" + 
        node.getGrammarSlot + 
        "\"), " + 
        node.getPivot + 
        ", " + 
        node.getParent.getObj + 
        ");\n")
      count += 1
      visitChildren(node)
      addChildren(node)
    }
  }

  override def visit(node: ListSymbolNode) {
    if (!node.isVisited) {
      node.setVisited(true)
      node.setObj("node" + count)
      if (grammar.isNewNonterminal(node.getGrammarSlot.asInstanceOf[HeadGrammarSlot])) {
        val index = grammar.getIndex(node.getGrammarSlot.asInstanceOf[HeadGrammarSlot])
        sb.append("ListSymbolNode node" + count + " = new ListSymbolNode(" + 
          "grammar.getNonterminalByNameAndIndex(\"" + 
          node.getGrammarSlot + 
          "\", " + 
          index + 
          "), " + 
          node.getLeftExtent + 
          ", " + 
          node.getRightExtent + 
          ");\n")
      } else {
        sb.append("ListSymbolNode node" + count + " = new ListSymbolNode(" + 
          "grammar.getNonterminalByName(\"" + 
          node.getGrammarSlot + 
          "\"), " + 
          node.getLeftExtent + 
          ", " + 
          node.getRightExtent + 
          ");\n")
      }
      count += 1
      visitChildren(node)
      addChildren(node)
    }
  }

  private def visitChildren(node: SPPFNode) {
    for (child <- node.getChildren) {
      child.accept(this)
    }
  }

  private def addChildren(node: SPPFNode) {
    for (child <- node.getChildren) {
      val childName = child.getObj.asInstanceOf[String]
      assert(childName != null)
      sb.append(node.getObj + ".addChild(" + childName + ");\n")
    }
  }

  override def toString(): String = sb.toString
}
