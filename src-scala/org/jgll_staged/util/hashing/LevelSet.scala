package org.jgll_staged.util.hashing

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class LevelSet[T <: Level](initalCapacity: Int = CuckooHashSet.DEFAULT_INITIAL_CAPACITY, decomposer: ExternalHasher[T])
  extends CuckooHashSet[T](initalCapacity, decomposer) {

  private var level: Int = _

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
