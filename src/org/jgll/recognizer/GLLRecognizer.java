package org.jgll.recognizer;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.util.Input;

public interface GLLRecognizer {

	public boolean recognize(Input input, Grammar grammar, String nonterminalName);
	
	public boolean recognize(Input input, int start, int end, BodyGrammarSlot slot);
	
	public void add(GrammarSlot label, GSSNode u, int inputIndex);

	public void pop(GSSNode u, int i);

	public GSSNode create(GrammarSlot L, GSSNode u, int i);
	
	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public int getCi();
	
	public GSSNode getCu();
	
	public void update(GSSNode gssNode, int inputIndex);
	
	public void recognitionError(GSSNode gssNode, int inputIndex);
	
}
