package org.jgll.util.logging

import java.util.logging.Formatter
import java.util.logging.LogRecord

class ParserLogFormatter extends Formatter {

  override def format(record: LogRecord): String = {
    val sb = new StringBuilder()
    sb.append(record.getLevel.getName).append(" : ")
    sb.append(record.getMessage)
    sb.append("\n")
    sb.toString
  }
}
