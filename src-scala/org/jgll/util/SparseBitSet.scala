package org.jgll.util

//remove if not needed
import scala.collection.JavaConversions._

class SparseBitSet {

  private var wordsMap: collection.mutable.Map[Long, Long] = collection.mutable.Map()

  def set(index: Long) {
    val wordIndex = index >> 6
    var l = wordsMap.getOrElse(wordIndex, 0L)
    val bitIndex = index.toInt & 0x3f
    val bitmask = 1L << bitIndex
    l |= bitmask
    wordsMap.put(wordIndex, l)
  }

  def get(index: Long): Boolean = {
    val wordIndex = index >> 6
    val value = wordsMap.getOrElse(wordIndex, return false)
    val bitIndex = index.toInt & 0x3f
    val bitmask = 1L << bitIndex
    (value & bitmask) != 0
  }
}
