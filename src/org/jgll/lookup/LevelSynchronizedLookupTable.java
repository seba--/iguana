package org.jgll.lookup;

import java.util.ArrayDeque;
import java.util.Queue;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.hashing.LevelSet;
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
	
	private LevelSet<Descriptor> u;
	
	private LevelSet<SPPFNode> currentLevelNonPackedNodes;
	
	private LevelSet<SPPFNode>[] forwardNonPackedNodes;
	
	private LevelSet<Descriptor>[] forwardDescriptors;
	
	private Queue<Descriptor> r;
	
	private Queue<Descriptor>[] forwardRs;
	
	private LevelSet<GSSNode> currentGssNodes;
	
	private LevelSet<GSSNode>[] forwardGssNodes;
	
	private int countGSSNodes;
	
	private CuckooHashSet<GSSNode> gssNodes = new CuckooHashSet<>();

	
	/**
	 * The number of descriptors waiting to be processed.
	 */
	private int size;
	
	/**
	 * The total number of descriptors added
	 */
	private int all;
	
	@SuppressWarnings("unchecked")
	public LevelSynchronizedLookupTable(Grammar grammar, Input input) {
		super(grammar, input.size());
		this.longestTerminalChain = grammar.getLongestTerminalChain();
		
		terminals = new TerminalSymbolNode[longestTerminalChain + 1][2];
		
		u = new LevelSet<>(getSize());
		r = new ArrayDeque<>();
		
		forwardDescriptors = new LevelSet[longestTerminalChain];
		forwardRs = new Queue[longestTerminalChain];
		
		currentLevelNonPackedNodes = new LevelSet<>();
		forwardNonPackedNodes = new LevelSet[longestTerminalChain];
		
		currentGssNodes = new LevelSet<>();
		forwardGssNodes = new LevelSet[longestTerminalChain];
		
		for(int i = 0; i < longestTerminalChain; i++) {
			forwardDescriptors[i] = new LevelSet<>(getSize());
			forwardRs[i] = new ArrayDeque<>();
			forwardNonPackedNodes[i] = new LevelSet<>();
			forwardGssNodes[i] = new LevelSet<>();
		}
	}
	
	private void gotoNextLevel() {
		int nextIndex = indexFor(currentLevel + 1);
		
		LevelSet<Descriptor> tmpDesc = u;
		u.clear();
		u = forwardDescriptors[nextIndex];
		forwardDescriptors[nextIndex] = tmpDesc;
		
		Queue<Descriptor> tmpR = r;
		assert r.isEmpty();
		r = forwardRs[nextIndex];
		forwardRs[nextIndex] = tmpR;
		
		LevelSet<SPPFNode> tmp = currentLevelNonPackedNodes;
		currentLevelNonPackedNodes.clear();
		currentLevelNonPackedNodes = forwardNonPackedNodes[nextIndex];
		forwardNonPackedNodes[nextIndex] = tmp;
		
		LevelSet<GSSNode> tmpGSSNodeSet = currentGssNodes;
		currentGssNodes.clear();
		currentGssNodes = forwardGssNodes[nextIndex];
		forwardGssNodes[nextIndex] = tmpGSSNodeSet;
		
		terminals[indexFor(currentLevel)][0] = null;
		terminals[indexFor(currentLevel)][1] = null;
		
		currentLevel++;
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % longestTerminalChain;
	}
	
	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		
		boolean newNodeCreated = false;
		SPPFNode key = createNonPackedNode(slot, leftExtent, rightExtent);
		SPPFNode value;
		
		if(rightExtent == currentLevel) {
			value = currentLevelNonPackedNodes.addAndGet(key);
			if(value == null){
				countNonPackedNodes++;
				newNodeCreated = true;
				value = key;
			}
		} else {
			int index = indexFor(rightExtent);
			value = forwardNonPackedNodes[index].addAndGet(key);
			if(value == null){
				countNonPackedNodes++;
				newNodeCreated = true;
				value = key;
			}
		}
		
		log.trace("SPPF node created: %s : %b", value, newNodeCreated);
		return value;
	}
	
	
	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		
		boolean newNodeCreated = false;
		
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
			newNodeCreated = true;
		}
		
		log.trace("SPPF Terminal node created: %s : %b", terminal, newNodeCreated);
		return terminal;
	}

	
	@Override
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol) {
		
		CuckooHashSet<SPPFNode> currentNodes;
		
		if(currentLevel == inputSize - 1) {
			currentNodes = currentLevelNonPackedNodes;
		} else {
			int index = indexFor(inputSize - 1); 
			currentNodes = forwardNonPackedNodes[index];
		}
		
		return (NonterminalSymbolNode) currentNodes.get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1));
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
	
	private int getSize() {
		return grammar.getMaxDescriptorsAtInput();
	}

	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		int inputIndex = descriptor.getInputIndex();
		if(inputIndex == currentLevel) {
			if(u.add(descriptor)) {
				 r.add(descriptor);
				 size++;
				 all++;
			} else {
				return false;
			}
		}
		
		else {
			int index = indexFor(descriptor.getInputIndex());
			if(forwardDescriptors[index].add(descriptor)) {
				forwardRs[index].add(descriptor);
				size++;
				all++;
			}  else {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public int getDescriptorsCount() {
		return all;
	}
	
	@Override
	public GSSNode getGSSNode(GrammarSlot label, int inputIndex) {
		GSSNode key = new GSSNode(label, inputIndex);
		GSSNode value;
		if(inputIndex == currentLevel) {
			value = currentGssNodes.addAndGet(key);
			if(value == null) {
				countGSSNodes++;
				value = key;
			}
		} else {
			int index = indexFor(inputIndex);
			value = forwardGssNodes[index].addAndGet(key);
			if(value == null) {
				countGSSNodes++;
				value = key;
			}
		}
		return value;
	}

	@Override
	public int getGSSNodesCount() {
		return countGSSNodes;
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		throw new UnsupportedOperationException();
	}
}
	
