package org.jgll_staged.util.hashing

import java.io.Serializable
import org.jgll_staged.util.hashing.hashfunction.HashFunction
//remove if not needed
import scala.collection.JavaConversions._

trait ExternalHasher[T] extends Serializable {

  def hash(t: T, f: HashFunction): Int

  def equals(t1: T, t2: T): Boolean
}
