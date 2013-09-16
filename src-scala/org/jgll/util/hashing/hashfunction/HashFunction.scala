package org.jgll.util.hashing.hashfunction

import java.io.Serializable
//remove if not needed
import scala.collection.JavaConversions._

trait HashFunction extends Serializable {

  def hash(k: Int): Int

  def hash(k1: Int, k2: Int): Int

  def hash(k1: Int, k2: Int, k3: Int): Int

  def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int): Int

  def hash(k1: Int, 
      k2: Int, 
      k3: Int, 
      k4: Int, 
      k5: Int): Int

  def hash(keys: Array[Int]): Int
}
