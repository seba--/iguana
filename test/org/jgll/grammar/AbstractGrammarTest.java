package org.jgll.grammar;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;

public abstract class AbstractGrammarTest {

	protected Grammar grammar;
	protected GrammarInterpreter rdParser;
	protected GrammarInterpreter levelParser;
	protected String outputDir;
	
	@Before
	public void init() {
		grammar = initGrammar();
		rdParser = new RecursiveDescentGrammarInterpreter();
		levelParser = new LevelSynchronizedGrammarInterpretter();
		outputDir = System.getProperty("user.home") + "/output";
	}
	
	protected abstract Grammar initGrammar();

	@SafeVarargs
	protected static <T> Set<T> set(T...objects) {
		Set<T>  set = new HashSet<>();
		for(T t : objects) {
			set.add(t);
		}
		return set;
	}
	
}
