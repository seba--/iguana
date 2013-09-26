package org.jgll.util.hashing

import java.io.Serializable
import org.jgll.util.hashing.hashfunction.HashFunction

trait ExternalHasher[T] extends Serializable {

  def hash(t: T, f: HashFunction): Int

  def equals(t1: T, t2: T): Boolean
}
