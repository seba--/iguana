package org.jgll.util.logging

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger

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
      logger.info(s.format(args: _*))
    }
  }

  def warning(s: String, args: Any*) {
    if (logger.isLoggable(Level.WARNING)) {
      logger.warning(s.format(args: _*))
    }
  }

  def error(s: String, args: Any*) {
    if (logger.isLoggable(Level.SEVERE)) {
      logger.severe(s.format(args: _*))
    }
  }

  def debug(s: String, args: Any*) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine(s.format(args: _*))
    }
  }

  def trace(s: String, args: Any*) {
    if (logger.isLoggable(Level.FINEST)) {
      logger.finest(s.format(args: _*))
    }
  }
}
