package org.jgll.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrammarBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(GrammarBuilder.class);

	Map<String, HeadGrammarSlot> nonterminalsMap;
	
	List<BodyGrammarSlot> slots;
	
	List<HeadGrammarSlot> nonterminals;
	
	int longestTerminalChain;
	
	String name;

	// Fields related to filtering	
	List<HeadGrammarSlot> newNonterminals;
	
	private Map<Set<Alternate>, HeadGrammarSlot> existingAlternates;
	
	private Map<String, Set<Filter>> filters;

	
	public GrammarBuilder(String name) {
		this.name = name;
		nonterminals = new ArrayList<>();
		slots = new ArrayList<>();
		nonterminalsMap = new HashMap<>();
		filters = new HashMap<>();
		existingAlternates = new HashMap<>();
		newNonterminals = new ArrayList<>();
	}
	
	public Grammar build() {
		initializeGrammarProrperties();
		return new Grammar(this);
	}
	
	public GrammarBuilder addRule(Rule rule) {
		
		Nonterminal head = rule.getHead();
		List<Symbol> body = rule.getBody();
		
		HeadGrammarSlot headGrammarSlot = getHeadGrammarSlot(head);
		
		BodyGrammarSlot currentSlot = null;
		
		if(body.size() == 0) {
			currentSlot = new EpsilonGrammarSlot(grammarSlotToString(head, body, 0), 0, new HashSet<Terminal>(), headGrammarSlot, rule.getObject());
			headGrammarSlot.addAlternate(new Alternate(currentSlot));
			slots.add(currentSlot);
		} 
		
		else {
			int index = 0;
			BodyGrammarSlot firstSlot = null;
			for (Symbol symbol : body) {
				if (symbol.isTerminal()) {
					currentSlot = new TerminalGrammarSlot(grammarSlotToString(head, body, index), index, currentSlot, (Terminal) symbol, headGrammarSlot);
				} else {
					HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
					currentSlot = new NonterminalGrammarSlot(grammarSlotToString(head, body, index), index, currentSlot, nonterminal, headGrammarSlot);
				}
				slots.add(currentSlot);

				if (index == 0) {
					firstSlot = currentSlot;
				}
				index++;
			}
			
			LastGrammarSlot lastGrammarSlot = new LastGrammarSlot(grammarSlotToString(head, body, index), index, currentSlot, headGrammarSlot, rule.getObject());
			slots.add(lastGrammarSlot);
			headGrammarSlot.addAlternate(new Alternate(firstSlot));
		}
		
		return this;
	}
	
	private HeadGrammarSlot getHeadGrammarSlot(Nonterminal nonterminal) {
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal.getName());
		
		if(headGrammarSlot == null) {
			headGrammarSlot = new HeadGrammarSlot(nonterminal);
			nonterminalsMap.put(nonterminal.getName(), headGrammarSlot);
			nonterminals.add(headGrammarSlot);
		}
		
		return headGrammarSlot;
	}
	
	private void initializeGrammarProrperties() {
		calculateLongestTerminalChain();
		calculateFirstSets();
		calculateFollowSets();
		setTestSets();
		setIds();
	}
	
	private static String grammarSlotToString(Nonterminal head, List<Symbol> body, int index) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(head.getName()).append(" ::= ");
		
		for(int i = 0; i < body.size(); i++) {
			if(i == index) {
				sb.append(". ");
			}
			sb.append(body.get(i)).append(" ");
		}
		
		sb.delete(sb.length() - 1, sb.length());
		
		if(index == body.size()) {
			sb.append(" .");
		}
		
		return sb.toString();
	}
	
	/**
	 * Calculates the longest chain of terminals in a body of a production rule. 
	 */
	private void calculateLongestTerminalChain() {
		
		int longestTerminalChain = 0;
		
		for(HeadGrammarSlot head : nonterminals) {
			for(Alternate alternate : head.getAlternates()) {
				BodyGrammarSlot slot = alternate.getFirstSlot();
				int length = 0; // The length of the longest terminal chain for this rule
				while(!(slot.isLastSlot())) {
					if(slot.isTerminalSlot()) {
						length++;
					} 
					else {
						// If a terminal is seen reset the length of the longest chain
						if(length > longestTerminalChain) {
							longestTerminalChain = length;
						}
						length = 0;
					}
					slot = slot.next;
				}
				if(length > longestTerminalChain) {
					longestTerminalChain = length;
				}
			}
		}
		
		this.longestTerminalChain = longestTerminalChain;
	}
	
	private void calculateFirstSets() {
		boolean changed = true;
		
		while(changed) {
			changed = false;
			for(HeadGrammarSlot head : nonterminals) {
				
				for(Alternate alternate : head.getAlternates()) {
					changed |= addFirstSet(head.getFirstSet(), alternate.getFirstSlot(), changed);					
 				}
			}
		}
	}
	
	/**
	 * Adds the first set of the current slot to the given set.
	 * 
	 * @param set
	 * @param currentSlot
	 * @param changed
	 * 
	 * @return true if adding any new terminals are added to the first set.
	 */
	private boolean addFirstSet(Set<Terminal> set, BodyGrammarSlot currentSlot, boolean changed) {
		
		if(currentSlot instanceof EpsilonGrammarSlot) {
			return set.add(Epsilon.getInstance()) || changed;
		}
		
		else if(currentSlot instanceof TerminalGrammarSlot) {
			return set.add(((TerminalGrammarSlot) currentSlot).getTerminal()) || changed;
		}
		
		else if(currentSlot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
			changed = set.addAll(nonterminalGrammarSlot.getNonterminal().getFirstSet()) || changed;
			if(nonterminalGrammarSlot.getNonterminal().isNullable()) {
				return addFirstSet(set, currentSlot.next, changed) || changed;
			}
			return changed;
		} 
		
		// ignore LastGrammarSlot
		else {
			return changed;
		}
	}
	
	
	private boolean isChainNullable(BodyGrammarSlot slot) {
		if(!(slot instanceof LastGrammarSlot)) {
			if(slot instanceof TerminalGrammarSlot) {
				return false;
			}
			
			NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
			return ntGrammarSlot.getNonterminal().isNullable() && isChainNullable(ntGrammarSlot.next);
		}
		
		return true;
	}
	
	private void calculateFollowSets() {
		boolean changed = true;
		
		while(changed) {
			changed = false;
			for(HeadGrammarSlot head : nonterminals) {
				
				for(Alternate alternate : head.getAlternates()) {
					BodyGrammarSlot currentSlot = alternate.getFirstSlot();
					
					while(!(currentSlot instanceof LastGrammarSlot)) {
						
						if(currentSlot instanceof NonterminalGrammarSlot) {
							
							NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
							BodyGrammarSlot next = currentSlot.next;
							
							// For rules of the form X ::= alpha B, add the follow set of X to the
							// follow set of B.
							if(next instanceof LastGrammarSlot) {
								changed |= nonterminalGrammarSlot.getNonterminal().getFollowSet().addAll(head.getFollowSet());
								break;
							}
							
							// For rules of the form X ::= alpha B beta, add the first set of beta to
							// the follow set of B.
							Set<Terminal> followSet = nonterminalGrammarSlot.getNonterminal().getFollowSet();
							changed |= addFirstSet(followSet, currentSlot.next, changed);
							
							// If beta is nullable, then add the follow set of X to the follow set of B.
							if(isChainNullable(next)) {
								changed |= nonterminalGrammarSlot.getNonterminal().getFollowSet().addAll(head.getFollowSet());
							}
						}
						
						currentSlot = currentSlot.next;
					}
				}
			}
		}
		
		for(HeadGrammarSlot head : nonterminals) {
			// Remove the epsilon which may have been added from nullable nonterminals 
			head.getFollowSet().remove(Epsilon.getInstance());
			
			// Add the EOF to all nonterminals as each nonterminal can be used as
			// the start symbol.
			head.getFollowSet().add(EOF.getInstance());
		}
	}
	
	private void setTestSets() {
		for(HeadGrammarSlot head : nonterminals) {
			for(Alternate alternate : head.getAlternates()) {
				
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
				if(currentSlot instanceof NonterminalGrammarSlot) {
					Set<Terminal> testSet = new HashSet<>();
					addFirstSet(testSet, currentSlot, false);
					if(testSet.contains(Epsilon.getInstance())) {
						testSet.addAll(head.getFollowSet());
					}
					testSet.remove(Epsilon.getInstance());
					((NonterminalGrammarSlot) currentSlot).setTestSet(testSet);
				}
			}
		}
	}
	
	private void setIds() {
		int i = 0;
		for(HeadGrammarSlot head : nonterminals) {
			head.setId(i++);
		}
		
		i = 0;
		for(BodyGrammarSlot slot : slots) {
			slot.setId(i++);
		}
	}
		
	public void filter() {
		for(Entry<String, Set<Filter>> entry : filters.entrySet()) {
			log.debug("Filtering {}.", entry.getKey());
			filter(nonterminalsMap.get(entry.getKey()), entry.getValue());
		}
		nonterminals.addAll(newNonterminals);
	}
	
	private void filter(HeadGrammarSlot head, Iterable<Filter> filters) {
		for(Filter filter : filters) {
			for(Alternate alt : head.getAlternates()) {
				if(match(filter, alt)) {
					log.debug("{} matched {}", filter, alt);
										
					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());
					
					HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filter.getChild()));
					if(newNonterminal == null) {
						newNonterminal = rewrite(alt, filter.getPosition(), filter.getChild());						
						filter(newNonterminal, filters);
						if(filter.isLeftMost()) {
							rewriteRightEnds(newNonterminal, filter.getChild());
						}
					} else {
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
					}
				}
			}
		}
	}
	
	private HeadGrammarSlot rewrite(Alternate alt, int position, List<Symbol> filteredAlternate) {
		HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(position);
		HeadGrammarSlot newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal());
		alt.setNonterminalAt(position, newNonterminal);
		newNonterminals.add(newNonterminal);
		
		List<Alternate> copy = copyAlternates(newNonterminal, filteredNonterminal.getAlternates());
		newNonterminal.setAlternates(copy);
		newNonterminal.remove(filteredAlternate);
		existingAlternates.put(new HashSet<>(copyAlternates(newNonterminal, copy)), newNonterminal);
		return newNonterminal;
	}
	
	private boolean match(Filter f, Alternate alt) {
		if(alt.match(f.getParent())) {
			if(alt.getNonterminalAt(f.getPosition()).contains(f.getChild())) {
				return true;
			}
		}
		return false;
	}
	
	private void rewriteRightEnds(HeadGrammarSlot head, List<Symbol> filteredAlternate) {
		for(Alternate alternate : head.getAlternates()) {
			if(! (alternate.isBinary(head) || alternate.isUnaryPrefix(head))) {
				continue;
			}
			HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) alternate.getLastSlot()).getNonterminal();
			
			if(nonterminal.contains(filteredAlternate)) {
				HeadGrammarSlot filteredNonterminal = alternate.getNonterminalAt(alternate.size() - 1);
				
				HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filteredAlternate));
				if(newNonterminal == null) {
					newNonterminal = rewrite(alternate, alternate.size() - 1, filteredAlternate);
					rewriteRightEnds(newNonterminal, filteredAlternate);
				} else {
					alternate.setNonterminalAt(alternate.size() - 1, newNonterminal);
				}
			}
		}
	}

	
	private List<Alternate> copyAlternates(HeadGrammarSlot head, List<Alternate> list) {
		List<Alternate> copyList = new ArrayList<>();
		for(Alternate alt : list) {
			copyList.add(copyAlternate(alt, head));
		}
		return copyList;
	}
	
	private Alternate copyAlternate(Alternate alternate, HeadGrammarSlot head) {
		BodyGrammarSlot copyFirstSlot = copySlot(alternate.getFirstSlot(), null, head);
		
		BodyGrammarSlot current = alternate.getFirstSlot().next;
		BodyGrammarSlot copy = copyFirstSlot;
		
		while(current != null) {
			copy = copySlot(current, copy, head);
			current = current.next;
		}
		 
		return new Alternate(copyFirstSlot);
	}
	
	private BodyGrammarSlot copySlot(BodyGrammarSlot slot, BodyGrammarSlot previous, HeadGrammarSlot head) {

		BodyGrammarSlot copy;
		
		if(slot.isLastSlot()) {
			copy = new LastGrammarSlot(slot.label, slot.position, previous, head, ((LastGrammarSlot) slot).getObject());
		} 
		else if(slot.isNonterminalSlot()) {
			NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) slot;
			copy = new NonterminalGrammarSlot(slot.label, slot.position, previous, ntSlot.getNonterminal(), head);
		} 
		else {
			copy = new TerminalGrammarSlot(slot.label, slot.position, previous, ((TerminalGrammarSlot) slot).getTerminal(), head);
		}

		slots.add(copy);
		return copy;
	}
		
	@SafeVarargs
	protected static <T> Set<T> set(T...objects) {
		Set<T>  set = new HashSet<>();
		for(T t : objects) {
			set.add(t);
		}
		return set;
	}


	/**
	 * 
	 * Adds the given filter to the set of filters. If a filter with the same nonterminal, alternate index, and
	 * alternate index already exists, only the given filter alternates are added to the existing filter,
	 * effectively updating the filter.
	 * 
	 * @param nonterminal
	 * @param alternateIndex
	 * @param position
	 * @param filterdAlternates
	 * 
	 */
	public void addFilter(String nonterminal, List<Symbol> parent, int position, List<Symbol> filteredAlternate) {
		Filter filter = new Filter(parent, position, filteredAlternate);

		if(filters.containsKey(nonterminal)) {
			filters.get(nonterminal).add(filter);
		} 
		else {
			Set<Filter> set = new HashSet<>();
			set.add(filter);
			filters.put(nonterminal, set);
		}
	}
	
}
