package org.jgll_staged.grammar

import java.util.BitSet
//remove if not needed
import scala.collection.JavaConversions._

trait Terminal extends Symbol {

  def `match`(i: Int): Boolean

  def getMatchCode(): String

  def asBitSet(): BitSet
}
