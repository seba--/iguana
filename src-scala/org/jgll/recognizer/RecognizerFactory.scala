package org.jgll.recognizer

trait RecognizerFactoryTrait {
  self: GLLRecognizerTrait
   with InterpretedGLLRecognizerTrait
   with PrefixGLLRecognizerTrait =>
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
}