package org.jgll_staged.util.hashing.hashfunction

//remove if not needed
import scala.collection.JavaConversions._

class SuperFastHash extends HashFunction {

  private var seed = 5

  def this(seed: Int) {
    this()
    this.seed = seed
  }

  override def hash(k: Int): Int = 0

  override def hash(k1: Int, k2: Int): Int = 0

  override def hash(k1: Int, k2: Int, k3: Int): Int = {
    var hash = seed
    var tmp: Int = 0
    hash += k1 & 0xFFFF
    tmp = ((k1 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash += k2 & 0xFFFF
    tmp = ((k2 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash += k3 & 0xFFFF
    tmp = ((k3 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash ^= hash << 3
    hash += hash >> 5
    hash ^= hash << 4
    hash += hash >> 17
    hash ^= hash << 25
    hash += hash >> 6
    hash
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int): Int = {
    var hash = seed
    var tmp: Int = 0
    hash += k1 & 0xFFFF
    tmp = ((k1 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash += k2 & 0xFFFF
    tmp = ((k2 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash += k3 & 0xFFFF
    tmp = ((k3 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash += k4 & 0xFFFF
    tmp = ((k4 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash ^= hash << 3
    hash += hash >> 5
    hash ^= hash << 4
    hash += hash >> 17
    hash ^= hash << 25
    hash += hash >> 6
    hash
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int, 
      k5: Int): Int = {
    var hash = seed
    var tmp: Int = 0
    hash += k1 & 0xFFFF
    tmp = ((k1 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash += k2 & 0xFFFF
    tmp = ((k2 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash += k3 & 0xFFFF
    tmp = ((k3 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash += k4 & 0xFFFF
    tmp = ((k4 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash += k5 & 0xFFFF
    tmp = ((k5 & 0xFFFF0000) >> 5) ^ hash
    hash = (hash << 16) ^ tmp
    hash += hash >> 11
    hash ^= hash << 3
    hash += hash >> 5
    hash ^= hash << 4
    hash += hash >> 17
    hash ^= hash << 25
    hash += hash >> 6
    hash
  }

  override def hash(keys: Array[Int]): Int = 0
}
