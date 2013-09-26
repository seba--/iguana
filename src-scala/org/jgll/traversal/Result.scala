package org.jgll.traversal

import scala.reflect.BeanProperty

object Result {

  private val _filter = new Result[Any](null)

  private val _skip = new Result[Any](null)

  def filter[K](): Result[K] = _filter.asInstanceOf[Result[K]]

  def skip[K](): Result[K] = _skip.asInstanceOf[Result[K]]

  def accept[K](k: K): Result[K] = {
    if (k == null) {
      throw new IllegalArgumentException(k + "should not be null.")
    }
    new Result[K](k)
  }
}

class Result[T] private (@BeanProperty var obj: T)