package org.jgll.grammar;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarVisitor {
	
	public static void visit(Grammar grammar, GrammarVisitAction action) {
		for(HeadGrammarSlot head : grammar.getNonterminals()) {
			visit(head, action);
		}
	}
	
	public static void visit(Iterable<HeadGrammarSlot> heads, GrammarVisitAction action) {
		for(HeadGrammarSlot head : heads) {
			visit(head, action);
		}		
	}

	public static void visit(HeadGrammarSlot root, GrammarVisitAction action) {
		action.visit(root);
		for(Alternate alternate : root.getAlternates()) {
			BodyGrammarSlot currentSlot = alternate.getFirstSlot();
			while(!(currentSlot instanceof LastGrammarSlot)) {
				if(currentSlot instanceof NonterminalGrammarSlot) {
					action.visit((NonterminalGrammarSlot)currentSlot);						
				} 
				else if (currentSlot instanceof TerminalGrammarSlot) {
					action.visit((TerminalGrammarSlot) currentSlot);
				} 
				else if (currentSlot instanceof KeywordGrammarSlot) {
					action.visit((KeywordGrammarSlot)currentSlot);
				}
				currentSlot = currentSlot.next;
			}
			action.visit((LastGrammarSlot)currentSlot);
		}
	}

}
