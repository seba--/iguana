package org.jgll_staged.parser

import org.jgll_staged.util.hashing.hashfunction.HashFunction
import org.jgll_staged.util.hashing.hashfunction.MurmurHash3
//remove if not needed
import scala.collection.JavaConversions._

object HashFunctions {

  private var murmur3: HashFunction = new MurmurHash3()

  def defaulFunction(): HashFunction = murmur3
}
