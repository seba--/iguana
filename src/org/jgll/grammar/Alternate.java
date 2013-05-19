package org.jgll.grammar;

import java.util.ArrayList;
import java.util.List;

public class Alternate {
	
	private final List<BodyGrammarSlot> symbols;
	
	private final BodyGrammarSlot firstSlot;
	
	public Alternate(BodyGrammarSlot firstSlot) {
		
		if(firstSlot == null) {
			throw new IllegalArgumentException("firstSlot cannot be null.");
		}
		
		this.firstSlot = firstSlot;

		symbols = new ArrayList<>();
		
		BodyGrammarSlot current = firstSlot;
		while(!current.isLastSlot()) {
			symbols.add(current);
			current = current.next();
		}
	}
	
	public Symbol get(int index) {
		return symbols.get(index).getSymbol();
	}
	
	public BodyGrammarSlot getFirstSlot() {
		return firstSlot;
	}
	
	public BodyGrammarSlot getBodyGrammarSlotAt(int index) {
		return symbols.get(index);
	}
	
	public BodyGrammarSlot getLastSlot() {
		return symbols.get(symbols.size() - 1);
	}
	
	public int size() {
		return symbols.size();
	}
	
	public HeadGrammarSlot getNonterminalAt(int index) {
		BodyGrammarSlot bodyGrammarSlot = symbols.get(index);
		
		if(!bodyGrammarSlot.isNonterminalSlot()) {
			throw new RuntimeException("The symbol at " + index + " should be a nonterminal.");
		}
		
		return ((NonterminalGrammarSlot)bodyGrammarSlot).getNonterminal();
	}
	
	public void setNonterminalAt(int index, HeadGrammarSlot head) {
		BodyGrammarSlot bodyGrammarSlot = symbols.get(index);
		
		if(!bodyGrammarSlot.isNonterminalSlot()) {
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
		if(! (symbols.get(0).isNonterminalSlot() && symbols.get(symbols.size() - 1).isNonterminalSlot())) {
			return false;
		}
		
		NonterminalGrammarSlot firstNonterminal = (NonterminalGrammarSlot) symbols.get(0);
		NonterminalGrammarSlot lastNonterminal = (NonterminalGrammarSlot) symbols.get(symbols.size() - 1);
		
		return head.getNonterminal().getName().equals(firstNonterminal.getNonterminal().getNonterminal().getName()) &&
			   head.getNonterminal().getName().equals(lastNonterminal.getNonterminal().getNonterminal().getName());
	}
	
	public boolean isUnaryPrefix(HeadGrammarSlot head) {
		
		if(isBinary(head)) {
			return false;
		}
		
		if(! symbols.get(0).isNonterminalSlot()) {
			return false;
		}
		
		NonterminalGrammarSlot firstNonterminal = (NonterminalGrammarSlot) symbols.get(0);
		
		return head.getNonterminal().getName().equals(firstNonterminal.getNonterminal().getNonterminal().getName());
	}
	
	public boolean isUnaryPostfix(HeadGrammarSlot head) {
		if(isBinary(head)) {
			return false;
		}
		
		if(! symbols.get(symbols.size() - 1).isNonterminalSlot()) {
			return false;
		}
		
		NonterminalGrammarSlot lastNonterminal = (NonterminalGrammarSlot) symbols.get(symbols.size() - 1);
		
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
			
			if(thisSlot.isTerminalSlot() && otherSlot.isTerminalSlot()) {
				if(!thisSlot.getSymbol().equals(otherSlot.getSymbol())) {
					return false;
				}				
			}
			else if(thisSlot.isNonterminalSlot() && otherSlot.isNonterminalSlot()) {				
				NonterminalGrammarSlot thisNt = (NonterminalGrammarSlot) thisSlot;
				NonterminalGrammarSlot otherNt = (NonterminalGrammarSlot) otherSlot;
				if(thisNt.getNonterminal() != otherNt.getNonterminal()) {
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
