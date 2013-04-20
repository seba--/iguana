package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.util.Input;
import org.jgll.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {
	
	private static final Logger log = LoggerFactory.getLogger(Grammar.class);
	
	private static final long serialVersionUID = 1L;
	
	private final List<HeadGrammarSlot> nonterminals;
	
	private final List<BodyGrammarSlot> slots;
	
	/**
	 * A map from nonterminal names to their corresponding head slots.
	 * This map is used to locate head grammar slots by name for parsing
	 * from any arbitrary nonterminal.
	 */
	private final Map<String, HeadGrammarSlot> nameToNonterminals;
	
	private final Map<String, BodyGrammarSlot> nameToSlots;
	
	private final String name;

	private int longestTerminalChain;

	/**
	 * Map from <Rule, Position> to their corresponding grammar slot.
	 */
	private final Map<Tuple<Rule, Integer>, BodyGrammarSlot> slotsMap;
	
	/**
	 * Mapping from each rule to its first grammar slot. 
	 */
	private final Map<Rule, BodyGrammarSlot> alternatesMap;	
	
	private final Map<BodyGrammarSlot, Rule> reverseAlternatesMap;
	
	/**
	 * Nonterminals which are introduced as the result of filtering
	 */
	private Map<Collection<Rule>, HeadGrammarSlot> filteredNonterminals = new LinkedHashMap<>();
	
	private Grammar(String name, List<HeadGrammarSlot> nonterminals, List<BodyGrammarSlot> slots, Map<Tuple<Rule, Integer>, 
					BodyGrammarSlot> slotsMap, Map<Rule, BodyGrammarSlot> alternatesMap) {
		this.name = name;
		this.nonterminals = nonterminals;
		this.slots = slots;
		this.nameToNonterminals = new HashMap<>();
		this.nameToSlots = new HashMap<>();
		this.slotsMap = slotsMap;
		this.alternatesMap = alternatesMap;
		this.reverseAlternatesMap = new HashMap<>();
		
		for(Entry<Rule, BodyGrammarSlot> e : alternatesMap.entrySet()) {
			reverseAlternatesMap.put(e.getValue(), e.getKey());
		}
		
		for(HeadGrammarSlot nontermianl : nonterminals) {
			nameToNonterminals.put(nontermianl.toString(), nontermianl);
		}
		
		for(BodyGrammarSlot slot : slots) {
			nameToSlots.put(slot.toString(), slot);
		}
	}
	
	private void initializeGrammarProrperties() {
		calculateLongestTerminalChain();
		calculateFirstSets();
		calculateFollowSets();
		setTestSets();
	}
	
	public static Grammar fromRules(String name, Iterable<Rule> rules) {
		
		long start = System.nanoTime();
		
		Map<Nonterminal, HeadGrammarSlot> nonterminalMap = new HashMap<>();
		List<BodyGrammarSlot> slots = new ArrayList<>();
		List<HeadGrammarSlot> nonterminals = new ArrayList<>();
		Map<Tuple<Rule, Integer>, BodyGrammarSlot> slotsMap = new HashMap<>();
		Map<Rule, BodyGrammarSlot> alternatesMap = new HashMap<>();

		for (Rule rule : rules) {
			if(!nonterminalMap.containsKey(rule.getHead())) {
				HeadGrammarSlot head = new HeadGrammarSlot(nonterminals.size(), rule.getHead());
				nonterminals.add(head);
				nonterminalMap.put(rule.getHead(), head);				
			}
		}
		
		for (Rule rule : rules) {
			BodyGrammarSlot slot = null;
			HeadGrammarSlot head = nonterminalMap.get(rule.getHead());
			int index = 0;
			
			if(rule.size() == 0) {
				slot = new EpsilonGrammarSlot(nonterminals.size() + slots.size(), grammarSlotToString(rule, 0), 0, new HashSet<Terminal>(), head, rule.getObject());
				head.addAlternate(slot);
				slots.add(slot);
				slotsMap.put(new Tuple<Rule, Integer>(rule, 0), slot);
				continue;
			}
			
			for (Symbol symbol : rule.getBody()) {
				if (symbol instanceof Terminal) {
					slot = new TerminalGrammarSlot(nonterminals.size() + slots.size(), grammarSlotToString(rule, index), index, slot, (Terminal) symbol, head);
				} else {
					slot = new NonterminalGrammarSlot(nonterminals.size() + slots.size(), grammarSlotToString(rule, index), index, slot, nonterminalMap.get(symbol), head);
				}
				slots.add(slot);
				slotsMap.put(new Tuple<Rule, Integer>(rule, index), slot);

				if (index == 0) {
					head.addAlternate(slot);
					alternatesMap.put(rule, slot);
				}
				index++;
			}
			LastGrammarSlot lastGrammarSlot = new LastGrammarSlot(slots.size() + nonterminals.size(), grammarSlotToString(rule, index), index, slot, head, rule.getObject());
			slots.add(lastGrammarSlot);
			slotsMap.put(new Tuple<Rule, Integer>(rule, index), lastGrammarSlot);
		}

		long end = System.nanoTime();
		Grammar grammar =  new Grammar(name, nonterminals, slots, slotsMap, alternatesMap);
		log.debug("Grammar graph creation completed in {} ms.", (end - start)/1000_000);

		start = System.nanoTime();
		grammar.initializeGrammarProrperties();
		end = System.nanoTime();
		log.debug("First and follow set computation completed in {} ms.", (end - start)/1000_000);
		
		return grammar;
	}
	
	public void code(Writer writer, String packageName) throws IOException {
	
		String header = Input.read(this.getClass().getResourceAsStream("ParserTemplate"));
		header = header.replace("${className}", name)
					   .replace("${packageName}", packageName);
		writer.append(header);
		
		// case L0:
		writer.append("case " + L0.getInstance().getId() + ":\n");
		L0.getInstance().codeParser(writer);
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			writer.append("// " + nonterminal + "\n");
			writer.append("case " + nonterminal.getId() + ":\n");
			writer.append("parse_" + nonterminal.getId() + "();\n");
			writer.append("break;\n");
		}
				
		for(BodyGrammarSlot slot : slots) {
			if(!(slot.previous instanceof TerminalGrammarSlot)) {
				writer.append("// " + slot + "\n");
				writer.append("case " + slot.getId() + ":\n");
				writer.append("parse_" + slot.getId() + "();\n");
				writer.append("break;\n");
			}
		}
		
		writer.append("} } }\n");
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			nonterminal.codeParser(writer);
		}
		
		writer.append("}");
	}
	
	private static String grammarSlotToString(Rule rule, int index) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(rule.getHead().getName()).append(" ::= ");
		
		for(int i = 0; i < rule.size(); i++) {
			if(i == index) {
				sb.append(". ");
			}
			sb.append(rule.getSymbolAt(i)).append(" ");
		}
		
		sb.delete(sb.length() - 1, sb.length());
		
		if(index == rule.size()) {
			sb.append(" .");
		}
		
		return sb.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public HeadGrammarSlot getNonterminal(int id) {
		return nonterminals.get(id);
	}
		
	public BodyGrammarSlot getGrammarSlot(int id) {
		return slots.get(id - nonterminals.size());
	}
		
	public List<HeadGrammarSlot> getNonterminals() {
		return nonterminals;
	}
	
	public List<BodyGrammarSlot> getGrammarSlots() {
		return slots;
	}
	
	public HeadGrammarSlot getNonterminalByName(String name) {
		return nameToNonterminals.get(name);
	}
	
	public BodyGrammarSlot getGrammarSlot(Rule rule, int position) {
		return slotsMap.get(new Tuple<Rule, Integer>(rule, position));
	}
	
	public BodyGrammarSlot getGrammarSlotByName(String name) {
		return nameToSlots.get(name);
	}
	
	public BodyGrammarSlot getAlternateHead(Rule rule) {
		return alternatesMap.get(rule);
	}
	
	public int getLongestTerminalChain() {
		return longestTerminalChain;
	}
	
	/**
	 * Filters the nonterminal at the given position of the rule by disallowing
	 * the nonterminal to produce the given filter rules. The rules which should
	 * be filtered should have the same nonterminal head as the nonterminal
	 * at the position.  
	 * 
	 * @param rule
	 * @param position
	 * @param filterList
	 * 
	 * @throws IllegalArgumentException if the symbol at the position is not a
	 * 									nonterminal.
	 * @throws IllegalArgumentException if filter rules's head is not the same
	 * 									as the nonterminal as the given position.
	 * 									
	 */
	public void filter(Iterable<Filter> filters) {
		
		for(Filter filter : filters) {
			
			Rule rule = filter.getRule();
			Set<Rule> filteredRules = filter.getFilteredRules();
			
			BodyGrammarSlot grammarSlot = slotsMap.get(new Tuple<>(rule, filter.getPosition()));
			assert grammarSlot instanceof NonterminalGrammarSlot;
			
			NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) grammarSlot;
			
			HeadGrammarSlot restrictedNonterminal = filteredNonterminals.get(filteredRules);
			
			if(restrictedNonterminal == null) {
							
				List<BodyGrammarSlot> filteredAlternates = new ArrayList<>();
				for(Rule restrictedRule : filteredRules) {
					filteredAlternates.add(alternatesMap.get(restrictedRule));
				}
				
				int id = filteredNonterminals.size() + 1;
				Nonterminal nonterminal = new Nonterminal(ntGrammarSlot.getNonterminal().getNonterminal().getName() + "_" + id);
				restrictedNonterminal = new HeadGrammarSlot(id, nonterminal, ntGrammarSlot.getNonterminal(), filteredAlternates);
				filteredNonterminals.put(filteredRules, restrictedNonterminal);
			}
	
			ntGrammarSlot.setNonterminal(restrictedNonterminal);
		}
		
		
		// Second level filtering
		Map<Tuple<Rule, Integer>, Rule> secondLevelFilter = new HashMap<>();
		for(Filter filter : filters) {
			Rule rule = filter.getRule();
			Set<Rule> filteredRules = filter.getFilteredRules();

			if(isBinaryRule(rule)) {
				for(Rule filterRule : filteredRules) {
					if(isUnaryPrefix(filterRule)) {
						secondLevelFilter.put(new Tuple<>(rule, 0), filterRule);
					} else if(isUnaryPostfix(filterRule)) {
						secondLevelFilter.put(new Tuple<>(rule, rule.size()), filterRule);
					}
				}
			}
		}

		
		if(!secondLevelFilter.isEmpty()) {
			Grammar grammarCopy = copy(this);
			
			// For each right most expression E which is derived from a binary
			// rule of the form E ::= . E op E in which the E is filtered a
			// unary operator, copy the nonterminal and apply the unary rule
			// there.
			for(Entry<Tuple<Rule, Integer>, Rule> e : secondLevelFilter.entrySet()) {
				
				if(isUnaryPrefix(e.getValue())) {
					
					HeadGrammarSlot headCopy = ((NonterminalGrammarSlot) grammarCopy.slotsMap.get(e.getKey())).getNonterminal();
					for(BodyGrammarSlot slot : getLastSlots(headCopy)) {
						
						if(!slot.isNonterminalSlot()) {
							continue;
						}
						
						HeadGrammarSlot newHead = ((NonterminalGrammarSlot) slot).getNonterminal();

						int id = filteredNonterminals.size() + 1;
						Nonterminal nonterminal = new Nonterminal(newHead.getNonterminal().getName() + "_" + id);
						List<BodyGrammarSlot> filteredAlternates = new ArrayList<>();
						filteredAlternates.add(grammarCopy.alternatesMap.get(e.getValue()));
						HeadGrammarSlot filtered = new HeadGrammarSlot(id, nonterminal, newHead, filteredAlternates);
						
						((NonterminalGrammarSlot) slot).setNonterminal(filtered);
						
						
						// Set the rewritten tree below it
						NonterminalGrammarSlot slotToBeFiltered = ((NonterminalGrammarSlot) slotsMap.get(e.getKey()));
						slotToBeFiltered.setNonterminal(headCopy);
					}
				}
			}
		}
		
	}
	
	private static Grammar copy(Grammar grammar) {
		
		Map<HeadGrammarSlot, HeadGrammarSlot> heads = new HashMap<>();
		
		Map<Tuple<Rule, Integer>, BodyGrammarSlot> slotsMap = new HashMap<>();
		
		Map<Rule, BodyGrammarSlot> alternatesMap = new HashMap<>();
		
		List<BodyGrammarSlot> slots = new ArrayList<>();
		
		for(HeadGrammarSlot nonterminal : grammar.nonterminals) {
			heads.put(nonterminal, new HeadGrammarSlot(nonterminal.getId(), new Nonterminal(nonterminal.getNonterminal().getName())));
		}
		
		for(HeadGrammarSlot nonterminal : grammar.filteredNonterminals.values()) {
			heads.put(nonterminal, new HeadGrammarSlot(nonterminal.getId(), new Nonterminal(nonterminal.getNonterminal().getName())));
		}
		
		List<HeadGrammarSlot> allNonterminals = new ArrayList<>();
		allNonterminals.addAll(grammar.nonterminals);
		allNonterminals.addAll(grammar.filteredNonterminals.values());
		
		for(HeadGrammarSlot head : allNonterminals) {
			
			HeadGrammarSlot headCopy = heads.get(head);
			
			for(BodyGrammarSlot alternate : head.getAlternates()) {
				
				Rule rule = grammar.reverseAlternatesMap.get(alternate);
				
				BodyGrammarSlot s = alternate;
				
				BodyGrammarSlot copy = null;
				
				int position = 0;
				
				while(s != null) {
					
					if(s.isTerminalSlot()) {
						copy = new TerminalGrammarSlot(s.getId(), s.getLabel(), s.getPosition(), copy, ((TerminalGrammarSlot)s).getTerminal(), s.getHead());
					} 
					
					else if (s.isNonterminalSlot()){
						HeadGrammarSlot nonterminal = heads.get(((NonterminalGrammarSlot)s).getNonterminal());
						assert nonterminal != null;
						copy = new NonterminalGrammarSlot(s.getId(), s.getLabel(), s.getPosition(), copy, nonterminal, s.getHead());
					} 
					
					else if (s.isLastSlot()) {
						copy = new LastGrammarSlot(s.getId(), s.getLabel(), s.getPosition(), copy, s.getHead(), ((LastGrammarSlot)s).getObject());
					}
					
					if(position == 0) {
						headCopy.addAlternate(copy);
						alternatesMap.put(rule, copy);
					}
					
					
					s = s.next;
					slotsMap.put(new Tuple<>(rule, position++), copy);
					slots.add(copy);
				}
			}
		}

		return new Grammar(grammar.name, new ArrayList<>(heads.values()), slots, slotsMap, alternatesMap);
	}
	
	private boolean isBinaryRule(Rule rule) {
		return rule.getHead().equals(rule.getSymbolAt(0)) &&
			   rule.getHead().equals(rule.getSymbolAt(rule.size() - 1));
	}
	
	private boolean isUnaryPrefix(Rule rule) {
		return ! rule.getHead().equals(rule.getSymbolAt(0)) &&
				 rule.getHead().equals(rule.getSymbolAt(rule.size() - 1));
	}
	
	private boolean isUnaryPostfix(Rule rule) {
		return ! rule.getHead().equals(rule.getSymbolAt(rule.size() - 1)) &&
				 rule.getHead().equals(rule.getSymbolAt(0));
	}
	
	private Iterable<BodyGrammarSlot> getLastSlots(HeadGrammarSlot head) {
		
		List<BodyGrammarSlot> slots = new ArrayList<>();
		
		for(BodyGrammarSlot slot : head.getAlternates()) {
			BodyGrammarSlot current = slot;
			while(!current.next.isLastSlot()) {
				current = current.next;
			}
			slots.add(current);
		}
		return slots;
	}
	
	public void replace(Rule rule, int index, BodyGrammarSlot newSlot) {
		
		if(rule == null) {
			throw new IllegalArgumentException("rule cannot be null.");
		}
		
		if(index < 0 || index > rule.size()) {
			throw new IllegalArgumentException("index " + index + " should be >= 0 and <= " + rule.size());
		}
		
		if(newSlot == null) {
			throw new IllegalArgumentException("newSlot cannot be null.");
		}
		
		Tuple<Rule, Integer> tuple = new Tuple<Rule, Integer>(rule, index);
		BodyGrammarSlot oldSlot = slotsMap.get(tuple );
		
		if(oldSlot.previous != null) {
			oldSlot.previous.next = newSlot;
			newSlot.previous = oldSlot.previous;
		} else {
			// First nonterminal
			oldSlot.head.replaceAlternate(oldSlot, newSlot);
		}
		
		if(oldSlot.next != null) {
			oldSlot.next.previous = newSlot;
			newSlot.next= oldSlot.next;
		}
		
		slotsMap.put(tuple, newSlot);
		newSlot.id = oldSlot.id;
		slots.set(newSlot.id - nonterminals.size(), newSlot);
	}
	
	@Override
	public String toString() {
		
		Set<Nonterminal> visitedHeads = new HashSet<>();
		
		Deque<HeadGrammarSlot> todoQueue = new ArrayDeque<>();
		
		StringBuilder sb = new StringBuilder();
		
		for(HeadGrammarSlot head : nonterminals) {
			todoQueue.add(head);
			visitedHeads.add(head.getNonterminal());
		}
		
		while(!todoQueue.isEmpty()) {
			HeadGrammarSlot head = todoQueue.poll();
			
			for(BodyGrammarSlot slot : head.getAlternates()) {
				sb.append(head + " ::= ");
				BodyGrammarSlot currentSlot = slot;
				do {
					if(currentSlot.isNonterminalSlot()) {
						HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
						if(!visitedHeads.contains(nonterminal.getNonterminal())) {
							todoQueue.add(nonterminal);
							visitedHeads.add(nonterminal.getNonterminal());
						}
					}
					sb.append(" ").append(currentSlot.getSymbolName());
					if(currentSlot.isLastSlot()) {
						sb.append("\n");
					}
				} 
				while((currentSlot = currentSlot.next) != null);
			}

		}
				
		return sb.toString();
	}
	
	
	/**
	 * Calculates the longest chain of terminals in a body of a production rule. 
	 */
	private void calculateLongestTerminalChain() {
		
		int longestTerminalChain = 0;
		
		for(HeadGrammarSlot head : nonterminals) {
			for(BodyGrammarSlot alternate : head.getAlternates()) {
				BodyGrammarSlot slot = alternate;
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
				
				for(BodyGrammarSlot alternate : head.getAlternates()) {
					changed |= addFirstSet(head.getFirstSet(), alternate, changed);					
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
				
				for(BodyGrammarSlot alternate : head.getAlternates()) {
					BodyGrammarSlot currentSlot = alternate;
					
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
			for(BodyGrammarSlot current : head.getAlternates()) {
				if(current instanceof NonterminalGrammarSlot) {
					Set<Terminal> testSet = new HashSet<>();
					addFirstSet(testSet, current, false);
					if(testSet.contains(Epsilon.getInstance())) {
						testSet.addAll(head.getFollowSet());
					}
					testSet.remove(Epsilon.getInstance());
					((NonterminalGrammarSlot) current).setTestSet(testSet);
				}
			}
		}
	}
		
}
