package org.jgll.recognizer;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.util.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGLLRecognizer implements GLLRecognizer {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractGLLRecognizer.class);
	
	protected Input input;
	
	/**
	 * The current input index.
	 */
	protected int ci;

	/**
	 * The current GSS node.
	 */
	protected GSSNode cu;

	/**
	 * u0 is the bottom of the GSS.
	 */
	protected GSSNode u0;
	
	/**
	 * 
	 */
	protected Grammar grammar;
	
	/**
	 * The nonterminal from which the parsing will be started.
	 */
	protected HeadGrammarSlot startSymbol;
	
	private Set<Descriptor> descriptorSet;
	
	private Deque<Descriptor> descriptorStack;
	
	private Map<GSSNode, GSSNode> gssNodes;
	
	@Override
	public final boolean recognize(Input input, Grammar grammar, String nonterminalName) {		
		HeadGrammarSlot startSymbol = grammar.getNonterminalByName(nonterminalName);
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + nonterminalName + " found");
		}
		
		this.input = input;
		this.grammar = grammar;
		this.startSymbol = startSymbol;
		
		init();
	
		long start = System.nanoTime();
	
		recognize(startSymbol);
		
		long end = System.nanoTime();

		logStatistics(end - start);

		if(descriptorSet.contains(new Descriptor(startSymbol, u0, input.size()))) {
			return true;
		} else {
			return false;
		}

	}
	
	private void logStatistics(long duration) {
		log.info("Parsing Time: {} ms", duration/1000000);
		
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		log.debug("Memory used: {} mb", (runtime.totalMemory() - runtime.freeMemory()) / mb);
//		log.debug("Descriptors: {}", lookupTable.getDescriptorsCount());
//		log.debug("GSSNodes: {}", lookupTable.getGSSNodes().size());
//		log.debug("Non-packed nodes: {}", lookupTable.getDescriptorsCount());
//		log.debug("GSS Nodes: {}", lookupTable.getGSSNodesCount());
//		log.debug("GSS Edges: {}", lookupTable.getGSSEdgesCount());
	}

	
	protected abstract void recognize(HeadGrammarSlot startSymbol);
	
	/**
	 * initialized the parser's state before a new parse.
	 */
	protected abstract void init();

	@Override
	public final void add(GrammarSlot label, GSSNode u, int inputIndex) {
		Descriptor descriptor = new Descriptor(label, u, inputIndex);
		if(descriptorSet.add(descriptor)) {
			log.trace("Descriptor added: {}", descriptor);
			descriptorStack.push(descriptor);
		} else {
			log.trace("Descriptor {} already exists.", descriptor);
		}
	}
	
	@Override
	public final void pop(GSSNode u, int i) {
		
		log.trace("Pop {}, {}, {}", u.getLabel(), i);
		
		if (u != u0) {
			
			// Add (u, i) to P
			u.addPoppedIndex(i);
			
			for(GSSNode node : u.getChildren()) {
				add(u.getLabel(), node, i);
			}			
		}
	}

	@Override
	public final GSSNode create(GrammarSlot L, GSSNode u, int i) {
		log.trace("GSSNode created: " +  L + ", " + i);
		GSSNode key = new GSSNode(L, i);

		GSSNode v = gssNodes.get(key);
		if(v == null) {
			v = key;
			gssNodes.put(key, v);
			return key;
		}
		
		for(int index : v.getPoppedIndices()) {
			add(L, u, index);
		}
		
		return v;
	}
	
	@Override
	public boolean hasNextDescriptor() {
		return !descriptorStack.isEmpty();
	}
	
	@Override
	public Descriptor nextDescriptor() {
		return descriptorStack.pop();
	}

}
