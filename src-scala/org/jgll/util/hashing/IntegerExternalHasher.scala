package org.jgll.util.hashing

import org.jgll.util.hashing.hashfunction.HashFunction
import IntegerExternalHasher._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object IntegerExternalHasher {

  @BeanProperty
  lazy val instance = new IntegerExternalHasher()
}

@SerialVersionUID(1L)
class IntegerExternalHasher private () extends ExternalHasher[Integer] {

  override def hash(t: java.lang.Integer, f: HashFunction): Int = f.hash(t)

  override def equals(t1: java.lang.Integer, t2: java.lang.Integer): Boolean = t1.intValue() == t2.intValue()
}
