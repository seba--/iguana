package org.jgll.grammar;

import static org.junit.Assert.*;
import static org.jgll.util.collections.CollectionsUtil.*;

import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * S ::= "if" L S L "then" L S 
 *     | s
 *     
 * L ::= " "    
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest2 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Nonterminal S = new Nonterminal("S");
		Keyword iff = new Keyword("if", new int[] {'i', 'f'});
		Keyword then = new Keyword("then", new int[] {'t', 'h', 'e', 'n'});
		Nonterminal L = new Nonterminal("L");
		Terminal s = new Character('s');
		Terminal ws = new Character(' ');
		
		Rule r1 = new Rule(S, iff, L, S, L, then, L, S);
		Rule r2 = new Rule(S, s);
		Rule r3 = new Rule(L, ws);
		
		return new GrammarBuilder().addRule(r1)
								   .addRule(r2)
								   .addRule(r3)
								   .addRule(GrammarBuilder.fromKeyword(iff))
								   .addRule(GrammarBuilder.fromKeyword(then)).build();
	}
	
	
	@Test
	public void testFirstSet() {
		assertEquals(set(new Character('i'), TerminalFactory.from('s')), grammar.getNonterminalByName("S").getFirstSet());
	}
	
	@Test
	public void testKeywordLength() {
		assertEquals(4, grammar.getLongestTerminalChain());
	}

	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("if s then s"), grammar, "S");
		generateSPPFGraph(sppf);
	}
	
}
