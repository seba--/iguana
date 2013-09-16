package org.jgll_staged.util.hashing

import java.util.Arrays
import org.jgll_staged.util.hashing.hashfunction.HashFunction
import IntArrayExternalHasher._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object IntArrayExternalHasher {

  @BeanProperty
  lazy val instance = new IntArrayExternalHasher()
}

@SerialVersionUID(1L)
class IntArrayExternalHasher extends ExternalHasher[Array[Int]] {

  override def hash(t: Array[Int], f: HashFunction): Int = f.hash(t)

  override def equals(t1: Array[Int], t2: Array[Int]): Boolean = Arrays.==(t1, t2)
}
