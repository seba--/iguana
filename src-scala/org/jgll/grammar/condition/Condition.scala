package org.jgll.grammar.condition

import java.io.Serializable
import org.jgll.grammar.condition.ConditionType._

@SerialVersionUID(1L)
abstract class Condition(protected var `type`: ConditionType) extends Serializable {

  def getType(): ConditionType = `type`
}
