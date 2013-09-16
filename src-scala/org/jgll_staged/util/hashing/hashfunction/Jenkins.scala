package org.jgll_staged.util.hashing.hashfunction

//remove if not needed
import scala.collection.JavaConversions._

class Jenkins extends HashFunction {

  private var seed: Int = 5

  def this(seed: Int) {
    this()
    this.seed = seed
  }

  override def hash(k: Int): Int = 0

  override def hash(k1: Int, k2: Int): Int = 0

  override def hash(k1: Int, k2: Int, k3: Int): Int = {
    var a: Int = 0
    var b: Int = 0
    var c: Int = 0
    a = b = c = 0xdeadbeef + seed
    a += k1
    b += k2
    c += k3
    a -= c
    a ^= java.lang.Integer.rotateLeft(c, 4)
    c += b
    b -= a
    b ^= java.lang.Integer.rotateLeft(a, 6)
    a += c
    c -= b
    c ^= java.lang.Integer.rotateLeft(b, 8)
    b += a
    a -= c
    a ^= java.lang.Integer.rotateLeft(c, 16)
    c += b
    b -= a
    b ^= java.lang.Integer.rotateLeft(a, 19)
    a += c
    c -= b
    c ^= java.lang.Integer.rotateLeft(b, 4)
    b += a
    c ^= b
    c -= java.lang.Integer.rotateLeft(b, 14)
    a ^= c
    a -= java.lang.Integer.rotateLeft(c, 11)
    b ^= a
    b -= java.lang.Integer.rotateLeft(a, 25)
    c ^= b
    c -= java.lang.Integer.rotateLeft(b, 16)
    a ^= c
    a -= java.lang.Integer.rotateLeft(c, 4)
    b ^= a
    b -= java.lang.Integer.rotateLeft(a, 14)
    c ^= b
    c -= java.lang.Integer.rotateLeft(b, 24)
    c
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int): Int = {
    var a: Int = 0
    var b: Int = 0
    var c: Int = 0
    a = b = c = 0xdeadbeef + seed
    a += k1
    b += k2
    c += k3
    a -= c
    a ^= java.lang.Integer.rotateLeft(c, 4)
    c += b
    b -= a
    b ^= java.lang.Integer.rotateLeft(a, 6)
    a += c
    c -= b
    c ^= java.lang.Integer.rotateLeft(b, 8)
    b += a
    a -= c
    a ^= java.lang.Integer.rotateLeft(c, 16)
    c += b
    b -= a
    b ^= java.lang.Integer.rotateLeft(a, 19)
    a += c
    c -= b
    c ^= java.lang.Integer.rotateLeft(b, 4)
    b += a
    a += k4
    c ^= b
    c -= java.lang.Integer.rotateLeft(b, 14)
    a ^= c
    a -= java.lang.Integer.rotateLeft(c, 11)
    b ^= a
    b -= java.lang.Integer.rotateLeft(a, 25)
    c ^= b
    c -= java.lang.Integer.rotateLeft(b, 16)
    a ^= c
    a -= java.lang.Integer.rotateLeft(c, 4)
    b ^= a
    b -= java.lang.Integer.rotateLeft(a, 14)
    c ^= b
    c -= java.lang.Integer.rotateLeft(b, 24)
    c
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int, 
      k5: Int): Int = {
    var a: Int = 0
    var b: Int = 0
    var c: Int = 0
    a = b = c = 0xdeadbeef + seed
    a += k1
    b += k2
    c += k3
    a -= c
    a ^= java.lang.Integer.rotateLeft(c, 4)
    c += b
    b -= a
    b ^= java.lang.Integer.rotateLeft(a, 6)
    a += c
    c -= b
    c ^= java.lang.Integer.rotateLeft(b, 8)
    b += a
    a -= c
    a ^= java.lang.Integer.rotateLeft(c, 16)
    c += b
    b -= a
    b ^= java.lang.Integer.rotateLeft(a, 19)
    a += c
    c -= b
    c ^= java.lang.Integer.rotateLeft(b, 4)
    b += a
    a += k4
    b += k5
    c ^= b
    c -= java.lang.Integer.rotateLeft(b, 14)
    a ^= c
    a -= java.lang.Integer.rotateLeft(c, 11)
    b ^= a
    b -= java.lang.Integer.rotateLeft(a, 25)
    c ^= b
    c -= java.lang.Integer.rotateLeft(b, 16)
    a ^= c
    a -= java.lang.Integer.rotateLeft(c, 4)
    b ^= a
    b -= java.lang.Integer.rotateLeft(a, 14)
    c ^= b
    c -= java.lang.Integer.rotateLeft(b, 24)
    c
  }

  override def hash(keys: Int*): Int = 0
}
