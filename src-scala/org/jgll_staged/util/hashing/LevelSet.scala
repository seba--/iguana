package org.jgll_staged.util.hashing

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class LevelSet[T <: Level](decomposer: ExternalHasher[T]) extends CuckooHashSet[T](decomposer) {

  private var level: Int = _

  def this(initalCapacity: Int, decomposer: ExternalHasher[T]) {
    super(initalCapacity, decomposer)
  }

  override def isEntryEmpty(t: T): Boolean = t == null || t.getLevel != level

  override def add(key: T): T = {
    level = key.getLevel
    super.add(key)
  }

  override def clear() {
    size = 0
    rehashCount = 0
    enlargeCount = 0
  }
}
