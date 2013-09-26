package org.jgll.util.hashing

import org.jgll.util.hashing.hashfunction.HashFunction
import scala.reflect.BeanProperty

object IntegerExternalHasher {

  @BeanProperty
  lazy val instance = new IntegerExternalHasher()
}

@SerialVersionUID(1L)
class IntegerExternalHasher private () extends ExternalHasher[Int] {

  override def hash(t: Int, f: HashFunction): Int = f.hash(t)

  override def equals(t1: Int, t2: Int): Boolean = t1 == t2
}
