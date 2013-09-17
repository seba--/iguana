package org.jgll.util.hashing

import java.io.Serializable
import org.jgll.util.hashing.hashfunction.HashFunction
import LevelMap._
//remove if not needed
import scala.collection.JavaConversions._

object LevelMap {

  class MapEntry[K <: Level, V](var k: K, var v: V) extends Level {

    def getKey(): K = k

    def getValue(): V = v

    def setValue(value: V): V = {
      this.v = value
      v
    }

    override def equals(obj: Any): Boolean = {
      if (!(obj.isInstanceOf[MapEntry[K,V]])) {
        return false
      }
      val other = obj.asInstanceOf[MapEntry[K, V]]
      k == other.k
    }

    override def getLevel(): Int = k.getLevel
  }
}

@SerialVersionUID(1L)
class LevelMap[K <: Level, V >: Null](decomposer: ExternalHasher[K], initalCapacity: Int = CuckooHashSet.DEFAULT_INITIAL_CAPACITY)
                                     (implicit val kman: Manifest[K], implicit val vman: Manifest[V]) extends Serializable {

  private var set: LevelSet[MapEntry[K, V]] = new LevelSet(new MapEntryExternalHasher(decomposer))

  def put(key: K, value: V): V = {
    val entry = set.add(new MapEntry[K, V](key, value))
    if (entry == null) {
      return null
    }
    entry.v
  }

  def get(key: K): V = {
    val entry = set.get(new MapEntry[K, V](key, null))
    if (entry != null) {
      return entry.getValue
    }
    null
  }

  def clear() {
    set.clear()
  }

  @SerialVersionUID(1L)
  class MapEntryExternalHasher(private var externalHasher: ExternalHasher[K])
      extends ExternalHasher[MapEntry[K, V]] {

    override def hash(e: MapEntry[K, V], f: HashFunction): Int = externalHasher.hash(e.k, f)

    override def equals(e1: MapEntry[K, V], e2: MapEntry[K, V]): Boolean = externalHasher.equals(e1.k, e2.k)
  }
}
