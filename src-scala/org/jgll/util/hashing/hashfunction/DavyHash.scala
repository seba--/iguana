package org.jgll.util.hashing.hashfunction

class DavyHash(private var seed: Int) extends HashFunction {

  private val m = 0x5bd1e995

  private val r = 24

  def this() {
    this(0)
  }

  private def mixK(_k: Int): Int = {
    var k = _k
    k *= m
    k ^= k >>> r
    k *= m
    k
  }

  private def mixH(_h: Int, k: Int): Int = {
    var h = _h
    h *= m
    h ^= k
    h
  }

  override def hash(k: Int): Int = 0

  override def hash(k1: Int, k2: Int): Int = 0

  override def hash(k1: Int, k2: Int, k3: Int): Int = {
    var h = seed
    var k = k1
    k *= m
    k *= k2 << 10
    k ^= k >>> 16
    k *= k3 << 10
    k ^= k >>> 8
    h *= k
    h *= m
    h ^= h >>> 13
    h
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int): Int = {
    var h = seed ^ 4
    var k = (k1 & 0xFFFF) | ((k2 & 0xFFFF) << 16)
    k *= h
    k ^= k >>> 16
    h *= k
    k = (k3 & 0xFFFF) | ((k4 & 0xFFFF) << 16)
    k *= h
    k ^= k >>> 16
    h *= k
    h *= m
    h ^= h >>> 13
    h *= m
    h ^= h >>> 15
    h
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int, 
      k5: Int): Int = {
    var h = seed ^ 4
    var k = (k1 & 0xFFFF) | ((k2 & 0xFFFF) << 16)
    k *= h
    k ^= k >>> 16
    h *= k
    k = (k3 & 0x3FF) | ((k4 & 0x3FF) << 10) | ((k5 & 0x3FF) << 20)
    k *= h
    k ^= k >>> 16
    h *= k
    h *= m
    h ^= h >>> 13
    h *= m
    h ^= h >>> 15
    h
  }

  override def hash(keys: Array[Int]): Int = 0
}
