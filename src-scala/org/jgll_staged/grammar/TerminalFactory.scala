package org.jgll_staged.grammar

//remove if not needed
import scala.collection.JavaConversions._

object TerminalFactory {

  def from(c: Int): Terminal = new java.lang.Character(c)

  def get(start: Int, end: Int): Terminal = {
    if (start == end) {
      return new java.lang.Character(start)
    }
    new Range(start, end)
  }
}
