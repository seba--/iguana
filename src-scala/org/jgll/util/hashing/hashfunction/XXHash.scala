package org.jgll.util.hashing.hashfunction

import XXHash._
//remove if not needed
import scala.collection.JavaConversions._

object XXHash {

  private val PRIME2 = 2246822519L.toInt

  private val PRIME3 = 3266489917L.toInt

  private val PRIME4 = 668265263

  private val PRIME5 = 0x165667b1
}

class XXHash(_seed: Int) extends HashFunction {

  private var seed: Int = _seed + PRIME5

  override def hash(k: Int): Int = 0

  override def hash(k1: Int, k2: Int): Int = 0

  override def hash(k1: Int, k2: Int, k3: Int): Int = {
    var h = seed
    h += k1 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h += k2 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h += k3 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h ^= h >>> 15
    h *= PRIME2
    h ^= h >>> 13
    h *= PRIME3
    h ^= h >>> 16
    h
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int): Int = {
    var h = seed
    h += k1 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h += k2 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h += k3 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h += k4 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h ^= h >>> 15
    h *= PRIME2
    h ^= h >>> 13
    h *= PRIME3
    h ^= h >>> 16
    h
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int, 
      k5: Int): Int = {
    var h = seed
    h += k1 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h += k2 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h += k3 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h += k4 * PRIME3
    h = java.lang.Integer.rotateLeft(h, 17) * PRIME4
    h ^= h >>> 15
    h *= PRIME2
    h ^= h >>> 13
    h *= PRIME3
    h ^= h >>> 16
    h
  }

  override def hash(keys: Array[Int]): Int = 0
}
