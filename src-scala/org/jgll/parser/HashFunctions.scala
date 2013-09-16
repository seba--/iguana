package org.jgll.parser

import org.jgll.util.hashing.hashfunction.HashFunction
import org.jgll.util.hashing.hashfunction.MurmurHash3
//remove if not needed
import scala.collection.JavaConversions._

object HashFunctions {

  private var murmur3: HashFunction = new MurmurHash3()

  def defaulFunction(): HashFunction = murmur3
}
