package org.jgll.util.hashing

import org.jgll.util.hashing.hashfunction.HashFunction
import scala.reflect.BeanProperty

object StringExternalHasher {

  @BeanProperty
  lazy val instance = new StringExternalHasher()
}

@SerialVersionUID(1L)
class StringExternalHasher extends ExternalHasher[String] {

  override def hash(s: String, f: HashFunction): Int = {
    val array = Array.ofDim[Int](s.length / 2 + 1)
    var i = 1
    while (i < s.length) {
      array(i - 1) = s.charAt(i - 1) | (s.charAt(i) << 16)
      i += 2
    }
    f.hash(array)
  }

  override def equals(s1: String, s2: String): Boolean = s1 == s2
}
