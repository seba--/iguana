package org.jgll.util.trie

import scala.reflect.BeanProperty
import scala.collection.mutable.ListBuffer

class Node[T] {

  @BeanProperty
  var edges: ListBuffer[Edge[T]] = ListBuffer()

  def size(): Int = edges.size

  def addChild(edge: Edge[T]) {
    edges += (edge)
  }
}
