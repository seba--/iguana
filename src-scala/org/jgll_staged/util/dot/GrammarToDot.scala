package org.jgll_staged.util.dot

import org.jgll_staged.util.dot.GraphVizUtil.EDGE
import org.jgll_staged.util.dot.GraphVizUtil.END_EDGE
import org.jgll_staged.util.dot.GraphVizUtil.NONTERMINAL_EDGE
import org.jgll_staged.util.dot.GraphVizUtil.NONTERMINAL_NODE
import org.jgll_staged.util.dot.GraphVizUtil.SLOT_NODE
import java.util.ArrayDeque
import java.util.Deque
import java.util.HashSet
import java.util.Set
import org.jgll_staged.grammar.Alternate
import org.jgll_staged.grammar.Grammar
import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.grammar.slot.NonterminalGrammarSlot
//remove if not needed
import scala.collection.JavaConversions._

object GrammarToDot {

  def toDot(grammar: Grammar): String = {
    val sb = new StringBuilder()
    val visitedHeads = new HashSet[HeadGrammarSlot]()
    val todoQueue = new ArrayDeque[HeadGrammarSlot]()
    for (head <- grammar.getNonterminals) {
      todoQueue.add(head)
      visitedHeads.add(head)
    }
    while (!todoQueue.isEmpty) {
      val head = todoQueue.poll()
      for (alternate <- head.getAlternates) {
        sb.append("\"" + getId(head) + "\"" + 
          String.format(NONTERMINAL_NODE, grammar.getNonterminalName(head)))
        sb.append(EDGE + "\"" + getId(head) + "\"" + "->" + "{\"" + getId(alternate.getFirstSlot) + 
          "\"}" + 
          "\n")
        var previousSlot: BodyGrammarSlot = null
        var currentSlot = alternate.getFirstSlot
        while (currentSlot != null) {
          if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
            val nonterminal = currentSlot.asInstanceOf[NonterminalGrammarSlot].getNonterminal
            if (!visitedHeads.contains(nonterminal)) {
              todoQueue.add(nonterminal)
              visitedHeads.add(nonterminal)
            }
            sb.append(NONTERMINAL_EDGE + "\"" + getId(currentSlot) + "\"" + 
              "->" + 
              "{\"" + 
              getId(nonterminal) + 
              "\"}" + 
              "\n")
          }
          if (previousSlot != null) {
            sb.append(EDGE + "\"" + getId(previousSlot) + "\"" + "->" + "{\"" + 
              getId(currentSlot) + 
              "\"}" + 
              "\n")
          }
          sb.append("\"" + getId(currentSlot) + "\"" + String.format(SLOT_NODE, currentSlot))
          previousSlot = currentSlot
          currentSlot = currentSlot.next()
        }
        sb.append(END_EDGE + "\"" + getId(previousSlot) + "\"" + "->" + 
          "{\"" + 
          getId(previousSlot.asInstanceOf[BodyGrammarSlot].getHead) + 
          "\"}" + 
          "\n")
        sb.append("\n")
      }
    }
    sb.toString
  }

  private def getId(head: HeadGrammarSlot): String = "n" + head.getId

  private def getId(slot: BodyGrammarSlot): String = "s" + slot.getId
}
