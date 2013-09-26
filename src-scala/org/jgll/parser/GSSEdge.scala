package org.jgll.parser

import org.jgll.sppf.{TerminalSymbolNodeTrait, SPPFNodeTrait}
import org.jgll.util.hashing.ExternalHasher
import org.jgll.util.hashing.Level
import org.jgll.util.hashing.hashfunction.HashFunction
import scala.reflect.BeanProperty

trait GSSEdgeTrait {
  self: SPPFNodeTrait
   with TerminalSymbolNodeTrait
   with GSSNodeTrait =>

  import GSSEdge._
  object GSSEdge {

    val externalHasher = new GSSEdgeExternalHasher()

    val levelBasedExternalHasher = new GSSEdgeExternalHasher()

    private def compare(first: SPPFNode, second: SPPFNode): Boolean = {
      if (first.isInstanceOf[TerminalSymbolNode] && second.isInstanceOf[TerminalSymbolNode]) {
        first.asInstanceOf[TerminalSymbolNode].getMatchedChar ==
          second.asInstanceOf[TerminalSymbolNode].getMatchedChar
      } else {
        first.getGrammarSlot == second.getGrammarSlot
      }
    }

    @SerialVersionUID(1L)
    class GSSEdgeExternalHasher extends ExternalHasher[GSSEdge] {

      override def hash(edge: GSSEdge, f: HashFunction): Int = {
        if (edge.sppfNode.isInstanceOf[TerminalSymbolNode]) {
          f.hash(Array(edge.src.getGrammarSlot.getId, edge.src.getInputIndex, edge.dst.getGrammarSlot.getId,
            edge.dst.getInputIndex, 31, edge.sppfNode.asInstanceOf[TerminalSymbolNode].getMatchedChar))
        } else {
          f.hash(Array(edge.src.getGrammarSlot.getId, edge.src.getInputIndex, edge.dst.getGrammarSlot.getId,
            edge.dst.getInputIndex, 17, edge.sppfNode.getGrammarSlot.getId))
        }
      }

      override def equals(e1: GSSEdge, e2: GSSEdge): Boolean = {
        e1.src.getGrammarSlot == e2.src.getGrammarSlot && e1.src.getInputIndex == e2.src.getInputIndex &&
          e1.dst.getGrammarSlot == e2.dst.getGrammarSlot &&
          e1.dst.getInputIndex == e2.dst.getInputIndex &&
          compare(e1.sppfNode, e2.sppfNode)
      }
    }

    @SerialVersionUID(1L)
    class GSSEdgeLevelBasedExternalHasher extends ExternalHasher[GSSEdge] {

      override def hash(edge: GSSEdge, f: HashFunction): Int = {
        if (edge.sppfNode.isInstanceOf[TerminalSymbolNode]) {
          f.hash(edge.src.getGrammarSlot.getId, edge.dst.getGrammarSlot.getId, edge.dst.getInputIndex,
            31, edge.sppfNode.asInstanceOf[TerminalSymbolNode].getMatchedChar)
        } else {
          f.hash(edge.src.getGrammarSlot.getId, edge.dst.getGrammarSlot.getId, edge.dst.getInputIndex,
            17, edge.sppfNode.getGrammarSlot.getId)
        }
      }

      def equals(e1: GSSEdge, e2: GSSEdge): Boolean = {
        e1.src.getGrammarSlot == e2.src.getGrammarSlot && e1.dst.getGrammarSlot == e2.dst.getGrammarSlot &&
          e1.dst.getInputIndex == e2.dst.getInputIndex &&
          compare(e1.sppfNode, e2.sppfNode)
      }
    }
  }

  class GSSEdge(private val src: GSSNode, @BeanProperty val sppfNode: SPPFNode, private val dst: GSSNode)
      extends Level {

    def getDestination(): GSSNode = dst

    override def hashCode(): Int = {
      externalHasher.hash(this, HashFunctions.defaulFunction())
    }

    override def equals(o: Any): Boolean = {
      if (this == o) {
        return true
      }
      if (!(o.isInstanceOf[GSSEdge])) {
        return false
      }
      val other = o.asInstanceOf[GSSEdge]
      src.getGrammarSlot == other.src.getGrammarSlot && src.getInputIndex == other.src.getInputIndex &&
        dst.getGrammarSlot == other.dst.getGrammarSlot &&
        dst.getInputIndex == other.dst.getInputIndex &&
        compare(sppfNode, other.sppfNode)
    }

    override def getLevel(): Int = src.getInputIndex
  }
}