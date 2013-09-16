package org.jgll.util.hashing

import java.util.ArrayList
import java.util.List
import org.jgll.util.hashing.hashfunction.HashFunction
//remove if not needed
import scala.collection.JavaConversions._

class HashFunctionBuilder(private var f: HashFunction) {

  private var list: List[Integer] = new ArrayList()

  def addInt(i: Int): HashFunctionBuilder = {
    list.add(i)
    this
  }

  def hash(): Int = {
    val array = Array.ofDim[Int](list.size)
    for (i <- 0 until array.length) {
      array(i) = list.get(i)
    }
    f.hash(array)
  }
}
