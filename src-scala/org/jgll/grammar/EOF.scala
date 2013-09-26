package org.jgll.grammar

trait EOFTrait { self: CharacterTrait =>
  @SerialVersionUID(1L)
  object EOF  extends Character(0) {

    override def toString(): String = "$"
  }
}