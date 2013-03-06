package org.jgll.grammar;

import java.util.Arrays;

import org.jgll.parser.GrammarInterpreter;
import org.jgll.sppf.NonterminalSymbolNode;
import org.junit.Test;

public class Gamma2Test extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		// S ::= S S S | S S | b
		Rule rule1 = new Rule.Builder().head(new Nonterminal("S"))
									   .body(new Nonterminal("S"), new Nonterminal("S"), new Nonterminal("S")).build();
		Rule rule2 = new Rule.Builder().head(new Nonterminal("S")).body(new Nonterminal("S"), new Nonterminal("S")).build();
		Rule rule3 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('b')).build();
		return Grammar.fromRules("gamma2", Arrays.asList(rule1, rule2, rule3));
	}
		
	@Test
	public void parse() {
		GrammarInterpreter parser = new GrammarInterpreter();
		NonterminalSymbolNode sppf = parser.parse("bb", grammar, "S");
	}

}
