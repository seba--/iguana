package org.jgll_staged.recognizer

//remove if not needed
import scala.collection.JavaConversions._

object RecognizerFactory {

  private var _contextFreeRecognizer: GLLRecognizer = _

  private var prefixRecognizer: GLLRecognizer = _

  def contextFreeRecognizer(): GLLRecognizer = {
    if (_contextFreeRecognizer == null) {
      _contextFreeRecognizer = new InterpretedGLLRecognizer()
      return _contextFreeRecognizer
    }
    _contextFreeRecognizer
  }

  def prefixContextFreeRecognizer(): GLLRecognizer = {
    if (prefixRecognizer == null) {
      prefixRecognizer = new PrefixGLLRecognizer()
      return prefixRecognizer
    }
    prefixRecognizer
  }
}
