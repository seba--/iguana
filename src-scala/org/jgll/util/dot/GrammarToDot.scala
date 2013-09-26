package org.jgll.util.dot

import org.jgll.util.dot.GraphVizUtil.EDGE
import org.jgll.util.dot.GraphVizUtil.END_EDGE
import org.jgll.util.dot.GraphVizUtil.NONTERMINAL_EDGE
import org.jgll.util.dot.GraphVizUtil.NONTERMINAL_NODE
import org.jgll.util.dot.GraphVizUtil.SLOT_NODE
import org.jgll.grammar.GrammarTrait
import org.jgll.grammar.slot._

import collection.mutable

trait GrammarToDotTrait {
  self: GrammarTrait
   with HeadGrammarSlotTrait
   with BodyGrammarSlotTrait
   with NonterminalGrammarSlotTrait =>
  object GrammarToDot {

    def toDot(grammar: Grammar): String = {
      val sb = new StringBuilder()
      val visitedHeads = mutable.Set[HeadGrammarSlot]()
      val todoQueue = mutable.Queue[HeadGrammarSlot]()
      for (head <- grammar.getNonterminals) {
        todoQueue += (head)
        visitedHeads.add(head)
      }
      while (!todoQueue.isEmpty) {
        val head = todoQueue.dequeue
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
                todoQueue += (nonterminal)
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
}