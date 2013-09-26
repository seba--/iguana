package org.jgll.grammar

trait TerminalFactoryTrait {
  self: TerminalTrait
   with CharacterTrait
   with RangeTrait =>
  object TerminalFactory {

    def from(c: Int): Terminal = new Character(c)

    def get(start: Int, end: Int): Terminal = {
      if (start == end) {
        return new Character(start)
      }
      new Range(start, end)
    }
  }
}