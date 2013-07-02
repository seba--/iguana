package org.jgll.lookup;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * Provides lookup functionality for the level-based processing of the input
 * in a GLL parser. 
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class LevelSynchronizedLookupTable extends AbstractLookupTable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(LevelSynchronizedLookupTable.class);

	private int currentLevel;
	
	private int countNonPackedNodes;

	private int longestTerminalChain;
	
	private TerminalSymbolNode[][] terminals;
	
	private CuckooHashSet u;
	
	private Map<SPPFNode, SPPFNode> currentLevelNonPackedNodes;
	
	private Map<SPPFNode, SPPFNode>[] forwardNonPackedNodes;
	
	private List<Descriptor>[] forwardDescriptors;
	
	private Queue<Descriptor> r;
	
	/**
	 * The number of descriptors waiting to be processed.
	 */
	private int size;
	
	/**
	 * The total number of descriptors added
	 */
	private int all;
	
	@SuppressWarnings("unchecked")
	public LevelSynchronizedLookupTable(Grammar grammar, int inputSize) {
		super(grammar, inputSize);
		this.longestTerminalChain = grammar.getLongestTerminalChain();
		
		terminals = new TerminalSymbolNode[longestTerminalChain + 1][2];
		
		u = new CuckooHashSet();
		r = new ArrayDeque<>();
		
		forwardDescriptors = new List[longestTerminalChain];
		
		currentLevelNonPackedNodes = new HashMap<>();
		forwardNonPackedNodes = new HashMap[longestTerminalChain];
	}
	
	private void gotoNextLevel() {
		u = new CuckooHashSet();
		List<Descriptor> list = forwardDescriptors[indexFor(currentLevel + 1)];
		for(Descriptor d : list) {
			u.add(d);
			r.add(d);
		}
		list.clear();

		currentLevelNonPackedNodes = forwardNonPackedNodes[indexFor(currentLevel + 1)];
		if(currentLevelNonPackedNodes == null) {
			currentLevelNonPackedNodes = new HashMap<>();
		}
		forwardNonPackedNodes[indexFor(currentLevel + 1)] = new HashMap<>();
		
		terminals[indexFor(currentLevel)][0] = null;
		terminals[indexFor(currentLevel)][1] = null;

		currentLevel++;
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % longestTerminalChain;
	}
	
	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		
		SPPFNode key = createNonPackedNode(slot, leftExtent, rightExtent);
		
		if(rightExtent == currentLevel) {
			SPPFNode value = currentLevelNonPackedNodes.get(key);
			if(value == null) {
				value = key;
				currentLevelNonPackedNodes.put(key, value);
				countNonPackedNodes++;
			}
			return value;
		} else {
			int index = indexFor(rightExtent);
			
			if(forwardNonPackedNodes[index] == null) {
				forwardNonPackedNodes[index] = new HashMap<>();
				forwardNonPackedNodes[index].put(key, key);
				countNonPackedNodes++;
				return key;
			} 
			
			SPPFNode value = forwardNonPackedNodes[index].get(key);
			if(value == null) {
				value = key;
				forwardNonPackedNodes[index].put(key, value);
				countNonPackedNodes++;
			}
			return value;
		}
	}
		
	
	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		
		int index2;
		int rightExtent;
		if(terminalIndex == -2) {
			rightExtent = leftExtent;
			index2 = 1;
		} else {
			rightExtent = leftExtent + 1;
			index2 = 0;
		}
		
		int index = indexFor(rightExtent);

		TerminalSymbolNode terminal = terminals[index][index2];
		if(terminal == null) {
			terminal = new TerminalSymbolNode(terminalIndex, leftExtent);
			countNonPackedNodes++;
			terminals[index][index2] = terminal;
		}
		
		return terminal;
	}

	
	@Override
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol) {
		Map<SPPFNode, SPPFNode> map;
		if(currentLevel == inputSize - 1) {
			map = currentLevelNonPackedNodes;
		} else {
			int index = indexFor(inputSize - 1); 
			if(forwardNonPackedNodes[index] == null) {
				return null;
			}
			map = forwardNonPackedNodes[index];
		}
		return (NonterminalSymbolNode) map.get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1));
	}

	@Override
	public int getNonPackedNodesCount() {
		return countNonPackedNodes;
	}

	@Override
	public boolean hasNextDescriptor() {
		return size > 0;
	}

	@Override
	public Descriptor nextDescriptor() {
		if(!r.isEmpty()) {
			size--;
			return r.remove();
		} else {
			gotoNextLevel();
			return nextDescriptor();
		}
	}
	
	private int getSize(Set<Descriptor> previous) {
		int size = previous.size() + previous.size() * grammar.getMaxDescriptorsAtInput();
		return size;
	}

	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		int inputIndex = descriptor.getInputIndex();
		if(inputIndex == currentLevel) {
			if(!u.contains(descriptor)) {
				 r.add(descriptor);
				 u.add(descriptor);
				 size++;
				 all++;
			} else {
				return false;
			}
		}
		
		else {
			int index = indexFor(descriptor.getInputIndex());
			if(forwardDescriptors[index] == null) {
				forwardDescriptors[index] = new ArrayList<>();
			}
			forwardDescriptors[index].add(descriptor);
			size++;
			all++;
		}
		
		return true;
	}

	@Override
	public int getDescriptorsCount() {
		return all;
	}

}