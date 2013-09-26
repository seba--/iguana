package org.jgll.util.hashing.hashfunction

import MurmurHash3._

object MurmurHash3 {

  private val C1 = 0xcc9e2d51

  private val C2 = 0x1b873593

  private val M = 5

  private val N = 0xe6546b64

  private def mixK(_k: Int): Int = {
    var k = _k
    k *= C1
    k = java.lang.Integer.rotateLeft(k, 15)
    k = k * C2
    k
  }

  private def mixH(_h: Int, k: Int): Int = {
    var h = _h
    h ^= k
    h = java.lang.Integer.rotateLeft(h, 13)
    h = h * M + N
    h
  }
}

@SerialVersionUID(1L)
class MurmurHash3(private var seed: Int) extends HashFunction {

  def this() {
    this(0)
  }

  override def hash(keys: Array[Int]): Int = {
    var h = seed
    var k = 0
    for (i <- 0 until keys.length) {
      k = keys(i)
      k = mixK(k)
      h = mixH(h, k)
    }
    h ^= keys.length
    h ^= h >>> 16
    h *= 0x85ebca6b
    h ^= h >>> 13
    h *= 0xc2b2ae35
    h ^= h >>> 16
    h
  }

  override def hash(a: Int): Int = {
    var h = seed
    var k = a
    k = mixK(k)
    h = mixH(h, k)
    h ^= 1
    h ^= h >>> 16
    h *= 0x85ebca6b
    h ^= h >>> 13
    h *= 0xc2b2ae35
    h ^= h >>> 16
    h
  }

  override def hash(a: Int, b: Int): Int = {
    var h = seed
    var k = a
    k = mixK(k)
    h = mixH(h, k)
    k = b
    k = mixK(k)
    h = mixH(h, k)
    h ^= 2
    h ^= h >>> 16
    h *= 0x85ebca6b
    h ^= h >>> 13
    h *= 0xc2b2ae35
    h ^= h >>> 16
    h
  }

  override def hash(a: Int, b: Int, c: Int): Int = {
    var h = seed
    var k = a
    k = mixK(k)
    h = mixH(h, k)
    k = b
    k = mixK(k)
    h = mixH(h, k)
    k = c
    k = mixK(k)
    h = mixH(h, k)
    h ^= 3
    h ^= h >>> 16
    h *= 0x85ebca6b
    h ^= h >>> 13
    h *= 0xc2b2ae35
    h ^= h >>> 16
    h
  }

  def hash(a: Int, 
      b: Int, 
      c: Int, 
      d: Int, 
      e: Int): Int = {
    var h = seed
    var k = a
    k = mixK(k)
    h = mixH(h, k)
    k = b
    k = mixK(k)
    h = mixH(h, k)
    k = c
    k = mixK(k)
    h = mixH(h, k)
    k = d
    k = mixK(k)
    h = mixH(h, k)
    k = e
    k = mixK(k)
    h = mixH(h, k)
    h ^= 5
    h ^= h >>> 16
    h *= 0x85ebca6b
    h ^= h >>> 13
    h *= 0xc2b2ae35
    h ^= h >>> 16
    h
  }

  def hash(a: Int, 
      b: Int, 
      c: Int, 
      d: Int): Int = {
    var h = seed
    var k = a
    k = mixK(k)
    h = mixH(h, k)
    k = b
    k = mixK(k)
    h = mixH(h, k)
    k = c
    k = mixK(k)
    h = mixH(h, k)
    k = d
    k = mixK(k)
    h = mixH(h, k)
    h ^= 4
    h ^= h >>> 16
    h *= 0x85ebca6b
    h ^= h >>> 13
    h *= 0xc2b2ae35
    h ^= h >>> 16
    h
  }
}
