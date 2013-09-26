package org.jgll.grammar

import java.util.BitSet
import scala.virtualization.lms.common.Base

trait TerminalTrait {
  self: Base =>
  trait Terminal extends Symbol {

    def `match`(i: Rep[Int]): Boolean

    def getMatchCode(): String

    def asBitSet(): BitSet
  }
}