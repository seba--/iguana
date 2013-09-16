package org.jgll_staged.util.logging

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import LoggerWrapper._
//remove if not needed
import scala.collection.JavaConversions._

object LoggerWrapper {

  def getLogger[T](clazz: Class[T]): LoggerWrapper = {
    new LoggerWrapper(Logger.getLogger(clazz.getName))
  }
}

class LoggerWrapper private (private var logger: Logger) {

  logger.setUseParentHandlers(false)

  val handler = new ConsoleHandler()

  logger.setLevel(Level.FINE)

  handler.setLevel(Level.FINE)

  handler.setFormatter(new ParserLogFormatter())

  logger.addHandler(handler)

  def info(s: String, args: Any*) {
    if (logger.isLoggable(Level.INFO)) {
      logger.info(String.format(s, args))
    }
  }

  def warning(s: String, args: Any*) {
    if (logger.isLoggable(Level.WARNING)) {
      logger.warning(String.format(s, args))
    }
  }

  def error(s: String, args: Any*) {
    if (logger.isLoggable(Level.SEVERE)) {
      logger.severe(String.format(s, args))
    }
  }

  def debug(s: String, args: Any*) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine(String.format(s, args))
    }
  }

  def trace(s: String, args: Any*) {
    if (logger.isLoggable(Level.FINEST)) {
      logger.finest(String.format(s, args))
    }
  }
}
