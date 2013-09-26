package org.jgll.util.hashing

import org.jgll.util.hashing.hashfunction.HashFunction
import scala.reflect.BeanProperty

object IntArrayExternalHasher {

  @BeanProperty
  lazy val instance = new IntArrayExternalHasher()
}

@SerialVersionUID(1L)
class IntArrayExternalHasher extends ExternalHasher[Array[Int]] {

  override def hash(t: Array[Int], f: HashFunction): Int = f.hash(t)

  override def equals(t1: Array[Int], t2: Array[Int]): Boolean = t1 == t2
}
