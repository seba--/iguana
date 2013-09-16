package org.jgll_staged.util.hashing.hashfunction

//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(1L)
class MurmurHash2(private var seed: Int) extends HashFunction {

  private val m = 0x5bd1e995

  private val r = 24

  def this() {
    this(0)
  }

  override def hash(a: Int, 
      b: Int, 
      c: Int, 
      d: Int): Int = {
    var h = seed ^ 4
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
    h *= m
    h ^= h >>> 13
    h *= m
    h ^= h >>> 15
    h
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

  override def hash(a: Int, b: Int, c: Int): Int = hash(0, a, b, c)

  override def hash(k: Int): Int = 0

  override def hash(k1: Int, k2: Int): Int = 0

  override def hash(keys: Array[Int]): Int = 0

  override def hash(a: Int, 
      b: Int, 
      c: Int, 
      d: Int, 
      e: Int): Int = {
    var h = seed ^ 4
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
    h *= m
    h ^= h >>> 13
    h *= m
    h ^= h >>> 15
    h
  }
}
