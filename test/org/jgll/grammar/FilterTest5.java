package org.jgll.grammar;

import static org.junit.Assert.assertEquals;

import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * E ::= E z   1
 *     > x E   2
 *     > E w   3
 *     > y E   4
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest5 extends AbstractGrammarTest {

	private Rule rule1;
	private Rule rule2;
	private Rule rule3;
	private Rule rule4;
	private Rule rule5;

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E z
		Nonterminal E = new Nonterminal("E");
		rule1 = new Rule(E, list(E, new Character('z')));
		builder.addRule(rule1);
		
		// E ::=  x E
		rule2 = new Rule(E, list(new Character('x'), E));
		builder.addRule(rule2);
		
		// E ::= E w
		rule3 = new Rule(E, list(E, new Character('w')));
		builder.addRule(rule3);
		
		// E ::= y E
		rule4 = new Rule(E, list(new Character('y'), E));
		builder.addRule(rule4);
		
		// E ::= a
		rule5 = new Rule(E, list(new Character('a')));
		builder.addRule(rule5);
		
		// (E, .E z, x E) 
		builder.addFilter(E, rule1, 0, rule2);
		
		// (E, .E z, y E) 
		builder.addFilter(E, rule1, 0, rule4);
		
		// (E, x .E, E w)
		builder.addFilter(E, rule2, 1, rule3);
		
		// (E, .E w, y E)
		builder.addFilter(E, rule3, 0, rule4);
		
		builder.filter();
		return builder.build();
	}

	@Test
	public void testParsers() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("xawz"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("xawz"), grammar, "E");
		assertEquals(sppf1, sppf2);
	}

}
