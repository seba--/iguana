package org.jgll.traversal

import org.jgll.sppf.IntermediateNode
import org.jgll.sppf.ListSymbolNode
import org.jgll.sppf.NonterminalSymbolNode
import org.jgll.sppf.PackedNode
import org.jgll.sppf.TerminalSymbolNode
//remove if not needed
import scala.collection.JavaConversions._

trait SPPFVisitor {

  def visit(node: TerminalSymbolNode): Unit

  def visit(node: NonterminalSymbolNode): Unit

  def visit(node: IntermediateNode): Unit

  def visit(node: PackedNode): Unit

  def visit(node: ListSymbolNode): Unit
}
