package org.jgll.grammar;

import org.jgll.lookup.LevelSynchronizedLookupTable;
import org.jgll.parser.AbstractGLLParser;
import org.jgll.sppf.DummyNode;

public class LevelSynchronizedGrammarInterpretter extends AbstractGLLParser {
	
	@Override
	protected void init() {
		lookupTable = new LevelSynchronizedLookupTable(grammar, input.size());
	}

	@Override
	protected void parse(HeadGrammarSlot startSymbol) {
		startSymbol.parse(this, input, u0, DummyNode.getInstance(), 0);
	}

}
