package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;

public class Alternate implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final List<BodyGrammarSlot> symbols;
	
	private final BodyGrammarSlot firstSlot;
	
	private BodyGrammarSlot condition;
	
	private final int index;
	
	public Alternate(BodyGrammarSlot firstSlot, int index) {
		
		if(firstSlot == null) {
			throw new IllegalArgumentException("firstSlot cannot be null.");
		}
		
		this.firstSlot = firstSlot;

		symbols = new ArrayList<>();
		
		this.index = index;
		
		BodyGrammarSlot current = firstSlot;
		
		if(firstSlot instanceof LastGrammarSlot) {
			symbols.add(firstSlot);
		}
		
		while(!(current instanceof LastGrammarSlot)) {
			symbols.add(current);
			current = current.next();
		}
	}
	
	public void setCondition(BodyGrammarSlot condition) {
		this.condition = condition;
	}
	
	public BodyGrammarSlot getCondition() {
		return condition;
	}
	
	public Symbol getSymbolAt(int index) {
		return symbols.get(index).getSymbol();
	}
	
	public BodyGrammarSlot getFirstSlot() {
		return firstSlot;
	}
	
	/**
	 * @return true if the alternate is of the form A ::= epsilon
	 */
	public boolean isEmpty() {
		return firstSlot instanceof EpsilonGrammarSlot;
	}
	
	public boolean isNullable() {
		if (isEmpty()) return true;
		
		BodyGrammarSlot slot = firstSlot;
		while(!(slot instanceof LastGrammarSlot)) {
			
			if(slot instanceof TerminalGrammarSlot || slot instanceof KeywordGrammarSlot)
				return false;
			
			if(slot instanceof NonterminalGrammarSlot && !slot.isNullable())
				return false;
			
			slot = slot.next();
		}
		
		return true;
	}
	
	public BodyGrammarSlot getBodyGrammarSlotAt(int index) {
		return symbols.get(index);
	}
	
	/**
	 * ::= alpha . x
	 * 
	 * @return
	 */
	public BodyGrammarSlot getLastBodySlot() {
		return symbols.get(symbols.size() - 1);
	}
	
	public int size() {
		return symbols.size();
	}
	
	public int getIndex() {
		return index;
	}
	
	public HeadGrammarSlot getNonterminalAt(int index) {
		BodyGrammarSlot bodyGrammarSlot = symbols.get(index);
		
		if(!(bodyGrammarSlot instanceof NonterminalGrammarSlot)) {
			throw new RuntimeException("The symbol at " + index + " should be a nonterminal.");
		}
		
		return ((NonterminalGrammarSlot)bodyGrammarSlot).getNonterminal();
	}
	
	public void setNonterminalAt(int index, HeadGrammarSlot head) {
		BodyGrammarSlot bodyGrammarSlot = symbols.get(index);
		
		if(!(bodyGrammarSlot instanceof NonterminalGrammarSlot)) {
			throw new RuntimeException("The symbol at " + index + " should be a nonterminal.");
		}
		
		((NonterminalGrammarSlot)bodyGrammarSlot).setNonterminal(head);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(BodyGrammarSlot s : symbols) {
			sb.append(s.getSymbol()).append(" ");
		}
		return sb.toString();
	}
	
	public boolean isBinary(HeadGrammarSlot head) {
		if(! (symbols.get(0) instanceof NonterminalGrammarSlot && symbols.get(symbols.size() - 1) instanceof NonterminalGrammarSlot)) {
			return false;
		}
		
		NonterminalGrammarSlot firstNonterminal = (NonterminalGrammarSlot) symbols.get(0);
		NonterminalGrammarSlot lastNonterminal = (NonterminalGrammarSlot) symbols.get(symbols.size() - 1);
		
		return head.getNonterminal().getName().equals(firstNonterminal.getNonterminal().getNonterminal().getName()) &&
			   head.getNonterminal().getName().equals(lastNonterminal.getNonterminal().getNonterminal().getName());
	}
	
	/**
	 * 
	 * Returns true if the alternate is of the form op E.
	 * In other words, head = symbols[symobls.size - 1]
	 * 
	 */
	public boolean isUnaryPrefix(HeadGrammarSlot head) {
		
		if(isBinary(head)) {
			return false;
		}
		
		int index = symbols.size() - 1;
		if(! (symbols.get(index) instanceof NonterminalGrammarSlot)) {
			return false;
		}
		
		NonterminalGrammarSlot firstNonterminal = (NonterminalGrammarSlot) symbols.get(index);
		
		return head.getNonterminal().getName().equals(firstNonterminal.getNonterminal().getNonterminal().getName());
	}
	
	public boolean isUnaryPostfix(HeadGrammarSlot head) {
		if(isBinary(head)) {
			return false;
		}
		
		int index = 0;
		if(! (symbols.get(index) instanceof NonterminalGrammarSlot)) {
			return false;
		}
		
		NonterminalGrammarSlot lastNonterminal = (NonterminalGrammarSlot) symbols.get(index);
		
		return head.getNonterminal().getName().equals(lastNonterminal.getNonterminal().getNonterminal().getName());
	}
	
	public boolean match(List<Symbol> list) {
		
		if(list.size() != symbols.size()) {
			return false;
		}
		
		for(int i = 0; i < symbols.size(); i++) {
			if(!symbols.get(i).getSymbol().equals(list.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		// TODO: change it
		return 31;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Alternate)) {
			return false;
		}
		
		Alternate other = (Alternate) obj;
		
		if(this.size() != other.size()) {
			return false;
		}
		
		for(int i = 0; i < this.size(); i++) {
			BodyGrammarSlot thisSlot = symbols.get(i);
			BodyGrammarSlot otherSlot = other.symbols.get(i);
			
			if(thisSlot instanceof TerminalGrammarSlot && otherSlot instanceof TerminalGrammarSlot) {
				if(!thisSlot.getSymbol().equals(otherSlot.getSymbol())) {
					return false;
				}				
			} else if(thisSlot instanceof KeywordGrammarSlot && otherSlot instanceof KeywordGrammarSlot){
				if(!((KeywordGrammarSlot) thisSlot).getKeyword().equals(((KeywordGrammarSlot) otherSlot).getKeyword())) {
					return false;
				}
			}
			else if(thisSlot instanceof NonterminalGrammarSlot && otherSlot instanceof NonterminalGrammarSlot) {				
				Nonterminal thisNt = ((NonterminalGrammarSlot) thisSlot).getNonterminal().getNonterminal();
				Nonterminal otherNt = ((NonterminalGrammarSlot) otherSlot).getNonterminal().getNonterminal();
				
				if(thisNt == null && otherNt == null) {
					continue;
				}
				
				if(!thisNt.equals(otherNt)) {
					return false;
				}
			}
			else {
				return false;
			}
		}
		
		return true;
	}
	
}
