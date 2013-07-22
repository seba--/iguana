package org.jgll.recognizer;

import org.jgll.grammar.GrammarSlot;

public class PrefixGLLRecognizer extends AbstractGLLRecognizer {
	
	@Override
	public void add(GrammarSlot slot, GSSNode u, int inputIndex) {
		if(slot == startSlot && u == u0) {
			descriptorStack.clear();
			recognized = true;
			return;
		}
		super.add(slot, u, inputIndex);
	}

}
