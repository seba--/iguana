package org.jgll.util.trie

import scala.reflect.{BeanProperty, BooleanBeanProperty}

class Edge[T](@BeanProperty var label: T, @BeanProperty var destination: Node[T])
    {

  override def toString(): String = label.toString
}
