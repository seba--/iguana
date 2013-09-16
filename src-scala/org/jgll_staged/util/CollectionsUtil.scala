package org.jgll_staged.util

import java.util.Arrays
import java.util.HashSet
import java.util.List
import java.util.Set
//remove if not needed
import scala.collection.JavaConversions._

object CollectionsUtil {

  def listToString[T](elements: List[T]): String = {
    val sb = new StringBuilder()
    for (t <- elements) {
      sb.append(t.toString).append(" ")
    }
    sb.toString
  }

  def set[T](objects: T*): Set[T] = {
    val set = new HashSet[T]()
    for (t <- objects) {
      set.add(t)
    }
    set
  }

  def list[T](objects: T*): List[T] = Arrays.asList(objects:_*)
}
