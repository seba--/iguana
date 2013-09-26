package org.jgll.util.hashing

import org.jgll.util.hashing.hashfunction.HashFunction
import scala.collection.mutable.ListBuffer

class HashFunctionBuilder(private var f: HashFunction) {

  private var list: ListBuffer[Integer] = ListBuffer()

  def addInt(i: Int): HashFunctionBuilder = {
    list += (i)
    this
  }

  def hash(): Int = {
    val array = Array.ofDim[Int](list.size)
    for (i <- 0 until array.length) {
      array(i) = list(i)
    }
    f.hash(array)
  }
}
