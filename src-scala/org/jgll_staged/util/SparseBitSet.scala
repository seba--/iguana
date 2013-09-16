package org.jgll_staged.util

import java.util.HashMap
import java.util.Map
//remove if not needed
import scala.collection.JavaConversions._

class SparseBitSet {

  private var wordsMap: Map[Long, Long] = new HashMap()

  def set(index: Long) {
    val wordIndex = index >> 6
    var l = wordsMap.get(wordIndex)
    if (l == null) {
      l = 0L
    }
    val bitIndex = index.toInt & 0x3f
    val bitmask = 1L << bitIndex
    l |= bitmask
    wordsMap.put(wordIndex, l)
  }

  def get(index: Long): Boolean = {
    val wordIndex = index >> 6
    val value = wordsMap.get(wordIndex)
    if (value == null) {
      return false
    }
    val bitIndex = index.toInt & 0x3f
    val bitmask = 1L << bitIndex
    (value & bitmask) != 0
  }
}
