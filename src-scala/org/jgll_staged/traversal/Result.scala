package org.jgll_staged.traversal

import Result._
import scala.reflect.{BeanProperty, BooleanBeanProperty}
//remove if not needed
import scala.collection.JavaConversions._

object Result {

  private val filter = new Result[Any](null)

  private val skip = new Result[Any](null)

  def filter[K](): Result[K] = filter.asInstanceOf[Result[K]]

  def skip[K](): Result[K] = skip.asInstanceOf[Result[K]]

  def accept[K](k: K): Result[K] = {
    if (k == null) {
      throw new IllegalArgumentException(k + "should not be null.")
    }
    new Result[K](k)
  }
}

class Result[T] private (@BeanProperty var `object`: T)
