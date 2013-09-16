package org.jgll_staged.recognizer

//remove if not needed
import scala.collection.JavaConversions._

object RecognizerFactory {

  private var contextFreeRecognizer: GLLRecognizer = _

  private var prefixRecognizer: GLLRecognizer = _

  def contextFreeRecognizer(): GLLRecognizer = {
    if (contextFreeRecognizer == null) {
      contextFreeRecognizer = new InterpretedGLLRecognizer()
      return contextFreeRecognizer
    }
    contextFreeRecognizer
  }

  def prefixContextFreeRecognizer(): GLLRecognizer = {
    if (prefixRecognizer == null) {
      prefixRecognizer = new PrefixGLLRecognizer()
      return prefixRecognizer
    }
    prefixRecognizer
  }
}
