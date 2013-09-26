package org.jgll.util.hashing

import java.io.Serializable
import org.jgll.util.hashing.hashfunction.HashFunction
import CuckooHashMap._

object CuckooHashMap {

  class MapEntry[K: Manifest, V: Manifest](var k: K, var v: V) {

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

    override def toString(): String = {
      "(" + k.toString + ", " + (if (v == null) "" else v.toString) +
        ")"
    }
  }
}

@SerialVersionUID(1L)
class CuckooHashMap[K, V >: Null](decomposer: ExternalHasher[K], initialCapacity: Int = CuckooHashSet.DEFAULT_INITIAL_CAPACITY)
                                 (implicit val kmanifest: Manifest[K], implicit val vmanifest: Manifest[V])
  extends Serializable {

  private var set: CuckooHashSet[MapEntry[K, V]] = new CuckooHashSet(initialCapacity, new MapEntryExternalHasher(decomposer))

  def get(key: K): V = {
    val entry = set.get(new MapEntry[K, V](key, null))
    if (entry != null) {
      return entry.getValue
    }
    null
  }

  def put(key: K, value: V): V = {
    val add = set.add(new MapEntry[K, V](key, value))
    if (add == null) {
      return null
    }
    add.v
  }

  def size(): Int = set.size

  def clear() {
    set.clear()
  }

  @SerialVersionUID(1L)
  class MapEntryExternalHasher(private var hasher: ExternalHasher[K]) extends ExternalHasher[MapEntry[K, V]] {

    override def hash(t: MapEntry[K, V], f: HashFunction): Int = hasher.hash(t.k, f)

    override def equals(e1: MapEntry[K, V], e2: MapEntry[K, V]): Boolean = hasher.equals(e1.k, e2.k)
  }
}
