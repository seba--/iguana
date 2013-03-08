package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.util.InputUtil;
import org.jgll.util.Tuple;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final List<HeadGrammarSlot> nonterminals;
	
	private final List<BodyGrammarSlot> slots;
	
	/**
	 * A map from nonterminal names to their corresponding head slots.
	 * This map is used to locate head grammar slots by name for parsing
	 * from any arbitrary nonterminal.
	 */
	private final Map<String, HeadGrammarSlot> nameToHeadSlots;
	
	private final String name;
	
	private int longestTerminalChain;
	
	private final Map<Tuple<Rule, Integer>, BodyGrammarSlot> slotsMap;
	
	private final Map<Rule, BodyGrammarSlot> alternatesMap;
	
	private Grammar(String name, List<HeadGrammarSlot> nonterminals, List<BodyGrammarSlot> slots, Map<Tuple<Rule, Integer>, 
					BodyGrammarSlot> slotsMap, Map<Rule, BodyGrammarSlot> alternatesMap) {
		this.name = name;
		this.nonterminals = Collections.unmodifiableList(nonterminals);
		this.slots = Collections.unmodifiableList(slots);
		this.nameToHeadSlots = new HashMap<>();
		this.slotsMap = slotsMap;
		for(HeadGrammarSlot startSymbol : nonterminals) {
			this.nameToHeadSlots.put(startSymbol.getName(), startSymbol);
		}
		this.alternatesMap = alternatesMap;
	}
	
	private void initializeGrammarProrperties() {
		calculateLongestTerminalChain();
		calculateFirstSets();
		calculateFollowSets();
		setTestSets();
	}
	
	public static Grammar fromRules(String name, Iterable<Rule> rules) {
		Map<Nonterminal, HeadGrammarSlot> nonterminalMap = new HashMap<>();
		List<BodyGrammarSlot> slots = new ArrayList<>();
		List<HeadGrammarSlot> nonterminals = new ArrayList<>();
		Map<Tuple<Rule, Integer>, BodyGrammarSlot> slotsMap = new HashMap<>();
		Map<Rule, BodyGrammarSlot> alternatesMap = new HashMap<>();

		for (Rule rule : rules) {
			if(!nonterminalMap.containsKey(rule.getHead())) {
				HeadGrammarSlot head = new HeadGrammarSlot(nonterminalMap.size(), rule.getHead());
				nonterminals.add(head);
				nonterminalMap.put(rule.getHead(), head);				
			}
		}

		for (Rule rule : rules) {
			BodyGrammarSlot slot = null;
			HeadGrammarSlot head = nonterminalMap.get(rule.getHead());
			int index = 0;
			
			if(rule.getBodyLength() == 0) {
				slot = new EpsilonGrammarSlot(slots.size(), 0, new HashSet<Terminal>(), head, rule.getObject());
				head.addAlternate(slot);
				slots.add(slot);
				slotsMap.put(new Tuple<Rule, Integer>(rule, 0), slot);
				continue;
			}
			
			for (Symbol symbol : rule.getBody()) {
				if (symbol instanceof Terminal) {
					slot = new TerminalGrammarSlot(slots.size(), index, slot, (Terminal) symbol);
				} else {
					slot = new NonterminalGrammarSlot(slots.size(), index, slot, nonterminalMap.get(symbol));
				}
				slots.add(slot);
				slotsMap.put(new Tuple<Rule, Integer>(rule, index), slot);

				if (index == 0) {
					head.addAlternate(slot);
					alternatesMap.put(rule, slot);
				}
				index++;
			}
			slots.add(new LastGrammarSlot(slots.size() + nonterminals.size(), index, slot, head, rule.getObject()));
		}

		Grammar grammar =  new Grammar(name, nonterminals, slots, slotsMap, alternatesMap);
		grammar.initializeGrammarProrperties();
		return grammar;
	}
	
	public void code(Writer writer, String packageName) throws IOException {
	
		String header = InputUtil.read(this.getClass().getResourceAsStream("ParserTemplate"));
		header = header.replace("${className}", name)
					   .replace("${packageName}", packageName);
		writer.append(header);
		
		// case L0:
		writer.append("case " + L0.getInstance().getId() + ":\n");
		L0.getInstance().code(writer);
		
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
			nonterminal.code(writer);
		}
		
		writer.append("}");
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
		return nameToHeadSlots.get(name);
	}
	
	public BodyGrammarSlot getGrammarSlot(Rule rule, int position) {
		return slotsMap.get(new Tuple<Rule, Integer>(rule, position));
	}
	
	public BodyGrammarSlot getAlternateHead(Rule rule) {
		return alternatesMap.get(rule);
	}
	
	public int getLongestTerminalChain() {
		return longestTerminalChain;
	}
	
	public void filter(Rule rule, int position, Set<Rule> filterList) {
		
		Map<Set<Rule>, HeadGrammarSlot> restrictedNonterminals = new HashMap<>();
		
		BodyGrammarSlot grammarSlot = slotsMap.get(new Tuple<Rule, Integer>(rule, position));
		assert grammarSlot instanceof NonterminalGrammarSlot;
		
		NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) grammarSlot;
		
			HeadGrammarSlot restrictedNonterminal = restrictedNonterminals.get(filterList);
			if(restrictedNonterminal == null) {
				
				List<BodyGrammarSlot> filteredAlternates = new ArrayList<>();
				for(Rule restrictedRule : filterList) {
					filteredAlternates.add(alternatesMap.get(restrictedRule));
				}
				
				restrictedNonterminal = new HeadGrammarSlot(ntGrammarSlot.getNonterminal(), filteredAlternates);
				restrictedNonterminals.put(filterList, restrictedNonterminal);
			}

			ntGrammarSlot.setNonterminal(restrictedNonterminal);
		}		
		
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			for(BodyGrammarSlot slot : nonterminal.getAlternates()) {
				sb.append(nonterminal.getName() + " ::= ");
				BodyGrammarSlot next = slot;
				do {
					sb.append(" ").append(next.getName());
					if(next instanceof LastGrammarSlot) {
						sb.append("\n");
					}
				} 
				while((next = next.next) != null);
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
				while(!(slot instanceof LastGrammarSlot)) {
					if(slot instanceof TerminalGrammarSlot) {
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
