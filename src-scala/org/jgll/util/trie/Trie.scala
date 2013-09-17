package org.jgll.util.trie

import java.util.Iterator
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

class Trie[T] {

  @BeanProperty
  var root: Node[T] = new Node()

  def add(label: T) {
    add(root, label, null)
  }

  def get(prefix: Iterable[T]): Node[T] = {
    var node = root
    for (label <- prefix) {
      node = getNodeWithEdgeLabel(node, label)
      if (node == null) {
        return null
      }
    }
    node
  }

  private def add(node: Node[T], label: T, `object`: AnyRef): Node[T] = {
    if (node.size == 0) {
      return insert(node, label)
    }
    val dest = getNodeWithEdgeLabel(node, label)
    if (dest == null) {
      insert(node, label)
    } else {
      dest
    }
  }

  private def getNodeWithEdgeLabel(node: Node[T], label: T): Node[T] = {
    node.getEdges.find(_.getLabel == label).map(_.getDestination)
      .getOrElse(null)
  }

  def add(labels: Iterable[T]) {
    add(labels, null)
  }

  def add(labels: Iterable[T], `object`: AnyRef) {
    var node = root
    for (label <- labels) {
      node = add(node, label, `object`)
    }
  }

  private def insert(node: Node[T], label: T): Node[T] = {
    val newNode = new Node[T]()
    node.addChild(new Edge[T](label, newNode))
    newNode
  }
}
