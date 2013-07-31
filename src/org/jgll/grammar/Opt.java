package org.jgll.grammar;

public class Opt extends Nonterminal {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Opt(Symbol s) {
		super(s.getName() + "?");
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}

}
