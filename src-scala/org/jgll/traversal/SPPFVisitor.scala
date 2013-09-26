package org.jgll.traversal

import org.jgll.sppf._
//remove if not needed

trait SPPFVisitorTrait {
  self: TerminalSymbolNodeTrait
   with NonterminalSymbolNodeTrait
   with IntermediateNodeTrait
   with PackedNodeTrait
   with ListSymbolNodeTrait
  =>
  trait SPPFVisitor {

    def visit(node: TerminalSymbolNode): Unit

    def visit(node: NonterminalSymbolNode): Unit

    def visit(node: IntermediateNode): Unit

    def visit(node: PackedNode): Unit

    def visit(node: ListSymbolNode): Unit
  }
}