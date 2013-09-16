package org.jgll.util.trie

import java.util.ArrayList
import java.util.List
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

class Node[T] {

  @BeanProperty
  var edges: List[Edge[T]] = new ArrayList()

  def size(): Int = edges.size

  def addChild(edge: Edge[T]) {
    edges.add(edge)
  }
}
