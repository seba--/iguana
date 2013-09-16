package org.jgll_staged.util.hashing.hashfunction

import JenkinsCWI._
//remove if not needed
import scala.collection.JavaConversions._

object JenkinsCWI {

  private def mix(a: Int, b: Int, c: Int): Int = {
    a -= b
    a -= c
    a ^= (c >> 13)
    b -= c
    b -= a
    b ^= (a << 8)
    c -= a
    c -= b
    c ^= (b >> 13)
    a -= b
    a -= c
    a ^= (c >> 12)
    b -= c
    b -= a
    b ^= (a << 16)
    c -= a
    c -= b
    c ^= (b >> 5)
    a -= b
    a -= c
    a ^= (c >> 3)
    b -= c
    b -= a
    b ^= (a << 10)
    c -= a
    c -= b
    c ^= (b >> 15)
    c
  }
}

class JenkinsCWI extends HashFunction {

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
    a = b = 0x9e3779b9
    c = seed
    a += k1
    b += k2
    c += k3
    a -= b
    a -= c
    a ^= (c >> 13)
    b -= c
    b -= a
    b ^= (a << 8)
    c -= a
    c -= b
    c ^= (b >> 13)
    a -= b
    a -= c
    a ^= (c >> 12)
    b -= c
    b -= a
    b ^= (a << 16)
    c -= a
    c -= b
    c ^= (b >> 5)
    a -= b
    a -= c
    a ^= (c >> 3)
    b -= c
    b -= a
    b ^= (a << 10)
    c -= a
    c -= b
    c ^= (b >> 15)
    mix(a, b, c)
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int): Int = {
    var a: Int = 0
    var b: Int = 0
    var c: Int = 0
    a = b = 0x9e3779b9
    c = seed
    a += k1
    b += k2
    c += k3
    a -= b
    a -= c
    a ^= (c >> 13)
    b -= c
    b -= a
    b ^= (a << 8)
    c -= a
    c -= b
    c ^= (b >> 13)
    a -= b
    a -= c
    a ^= (c >> 12)
    b -= c
    b -= a
    b ^= (a << 16)
    c -= a
    c -= b
    c ^= (b >> 5)
    a -= b
    a -= c
    a ^= (c >> 3)
    b -= c
    b -= a
    b ^= (a << 10)
    c -= a
    c -= b
    c ^= (b >> 15)
    a += k4
    mix(a, b, c)
  }

  override def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int, 
      k5: Int): Int = {
    var a: Int = 0
    var b: Int = 0
    var c: Int = 0
    a = b = 0x9e3779b9
    c = seed
    a += k1
    b += k2
    c += k3
    a -= b
    a -= c
    a ^= (c >> 13)
    b -= c
    b -= a
    b ^= (a << 8)
    c -= a
    c -= b
    c ^= (b >> 13)
    a -= b
    a -= c
    a ^= (c >> 12)
    b -= c
    b -= a
    b ^= (a << 16)
    c -= a
    c -= b
    c ^= (b >> 5)
    a -= b
    a -= c
    a ^= (c >> 3)
    b -= c
    b -= a
    b ^= (a << 10)
    c -= a
    c -= b
    c ^= (b >> 15)
    a += k4
    b += k5
    mix(a, b, c)
  }

  override def hash(keys: Int*): Int = 0
}
