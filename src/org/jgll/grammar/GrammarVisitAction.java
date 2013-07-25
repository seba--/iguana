package org.jgll.grammar;

import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;

public interface GrammarVisitAction {

	public void visit(HeadGrammarSlot head);
	
	public void visit(NonterminalGrammarSlot slot);
	
	public void visit(TerminalGrammarSlot slot);
	
	public void visit(LastGrammarSlot slot);
	
	public void visit(KeywordGrammarSlot slot);
}
