package org.jgll_staged.util.dot

import org.jgll_staged.sppf.DummyNode
import org.jgll_staged.sppf.NonPackedNode
import org.jgll_staged.sppf.PackedNode
import org.jgll_staged.sppf.SPPFNode
import org.jgll_staged.sppf.TerminalSymbolNode
//remove if not needed
import scala.collection.JavaConversions._

abstract class ToDot {

  protected def getId(node: SPPFNode): String = {
    if (node.isInstanceOf[TerminalSymbolNode]) {
      return getId(node.asInstanceOf[TerminalSymbolNode])
    } else if (node.isInstanceOf[NonPackedNode]) {
      return getId(node.asInstanceOf[NonPackedNode])
    } else if (node.isInstanceOf[PackedNode]) {
      return getId(node.asInstanceOf[PackedNode])
    } else if (node.isInstanceOf[DummyNode]) {
      return "-1"
    }
    throw new RuntimeException("Node of type " + node.getClass + " could not be matched.")
  }

  protected def getId(t: TerminalSymbolNode): String = {
    "t" + t.getMatchedChar + "," + t.getLeftExtent + "," + 
      t.getRightExtent
  }

  protected def getId(n: NonPackedNode): String = {
    n.getGrammarSlot.getId + "," + n.getLeftExtent + "," + 
      n.getRightExtent
  }

  protected def getId(p: PackedNode): String = {
    getId(p.getParent.asInstanceOf[NonPackedNode]) + "," + 
      p.getGrammarSlot.getId + 
      "," + 
      p.getPivot
  }
}
