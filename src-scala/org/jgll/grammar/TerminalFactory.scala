package org.jgll.grammar

//remove if not needed
import scala.collection.JavaConversions._

object TerminalFactory {

  def from(c: Int): Terminal = new Character(c)

  def get(start: Int, end: Int): Terminal = {
    if (start == end) {
      return new Character(start)
    }
    new Range(start, end)
  }
}
