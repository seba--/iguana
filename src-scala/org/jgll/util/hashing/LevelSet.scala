package org.jgll.util.hashing

@SerialVersionUID(1L)
class LevelSet[T >: Null <: Level](decomposer: ExternalHasher[T], initalCapacity: Int = CuckooHashSet.DEFAULT_INITIAL_CAPACITY)
                          (implicit val m: Manifest[T])
  extends CuckooHashSet[T](initalCapacity, decomposer) {

  private var level: Int = _

  override def isEntryEmpty(t: T): Boolean = t == null || t.getLevel != level

  override def add(key: T): T = {
    level = key.getLevel
    super.add(key)
  }

  override def clear() {
    _size = 0
    rehashCount = 0
    enlargeCount = 0
  }
}
