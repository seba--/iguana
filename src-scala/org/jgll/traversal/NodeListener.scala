package org.jgll.traversal

trait NodeListener[T, U] extends PositionInfoTrait {

  def startNode(`type`: T): Unit

  def endNode(`type`: T, children: Iterable[U], node: Rep[PositionInfo]): Result[U]

  def buildAmbiguityNode(children: Iterable[U], node: Rep[PositionInfo]): Result[U]

  def terminal(c: Int, node: Rep[PositionInfo]): Result[U]
}
