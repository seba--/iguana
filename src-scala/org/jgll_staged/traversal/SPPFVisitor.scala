package org.jgll_staged.traversal

import org.jgll_staged.sppf.IntermediateNode
import org.jgll_staged.sppf.ListSymbolNode
import org.jgll_staged.sppf.NonterminalSymbolNode
import org.jgll_staged.sppf.PackedNode
import org.jgll_staged.sppf.TerminalSymbolNode
//remove if not needed
import scala.collection.JavaConversions._

trait SPPFVisitor {

  def visit(node: TerminalSymbolNode): Unit

  def visit(node: NonterminalSymbolNode): Unit

  def visit(node: IntermediateNode): Unit

  def visit(node: PackedNode): Unit

  def visit(node: ListSymbolNode): Unit
}
