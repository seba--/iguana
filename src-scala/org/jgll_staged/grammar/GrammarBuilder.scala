package org.jgll_staged.grammar

import java.io.Serializable
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Deque
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.List
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import org.jgll_staged.grammar.condition.Condition
import org.jgll_staged.grammar.condition.ContextFreeCondition
import org.jgll_staged.grammar.condition.KeywordCondition
import org.jgll_staged.grammar.condition.TerminalCondition
import org.jgll_staged.grammar.grammaraction.LongestTerminalChainAction
import org.jgll_staged.grammar.patterns.AbstractPattern
import org.jgll_staged.grammar.patterns.ExceptPattern
import org.jgll_staged.grammar.patterns.PrecedencePattern
import org.jgll_staged.grammar.slot.BodyGrammarSlot
import org.jgll_staged.grammar.slot.DirectNullableNonterminalGrammarSlot
import org.jgll_staged.grammar.slot.EpsilonGrammarSlot
import org.jgll_staged.grammar.slot.HeadGrammarSlot
import org.jgll_staged.grammar.slot.KeywordGrammarSlot
import org.jgll_staged.grammar.slot.LastGrammarSlot
import org.jgll_staged.grammar.slot.NonterminalGrammarSlot
import org.jgll_staged.grammar.slot.TerminalGrammarSlot
import org.jgll_staged.grammar.slotaction.LineActions
import org.jgll_staged.grammar.slotaction.NotFollowActions
import org.jgll_staged.grammar.slotaction.NotMatchActions
import org.jgll_staged.grammar.slotaction.NotPrecedeActions
import org.jgll_staged.util.logging.LoggerWrapper
import org.jgll_staged.util.trie.Edge
import org.jgll_staged.util.trie.Node
import org.jgll_staged.util.trie.Trie
import GrammarBuilder._
//remove if not needed
import scala.collection.JavaConversions._

import org.jgll_staged.grammar.condition.ConditionType._

object GrammarBuilder {

  private val log = LoggerWrapper.getLogger(classOf[GrammarBuilder])

  def fromKeyword(keyword: Keyword): Rule = {
    val builder = new Rule.Builder(new Nonterminal(keyword.getName))
    for (i <- keyword.getChars) {
      builder.addSymbol(new Character(i))
    }
    builder.build()
  }

  // @SafeVarargs
  protected def set[T](objects: T*): Set[T] = {
    val set = new HashSet[T]()
    for (t <- objects) {
      set.add(t)
    }
    set
  }
}

@SerialVersionUID(1L)
class GrammarBuilder(var name: String) extends Serializable {

  var nonterminalsMap: Map[Nonterminal, HeadGrammarSlot] = new HashMap()

  var slots: List[BodyGrammarSlot] = _

  var nonterminals: List[HeadGrammarSlot] = new ArrayList()

  var longestTerminalChain: Int = _

  var maximumNumAlternates: Int = _

  var maxDescriptors: Int = _

  var averageDescriptors: Int = _

  var stDevDescriptors: Double = _

  var newNonterminalsMap: Map[Nonterminal, List[HeadGrammarSlot]] = new LinkedHashMap()

  private var existingAlternates: Map[Set[Alternate], HeadGrammarSlot] = new HashMap()

  private var precednecePatternsMap: Map[Nonterminal, List[PrecedencePattern]] = new HashMap()

  private var exceptPatterns: List[ExceptPattern] = new ArrayList()

  private var ruleToLastSlotMap: Map[Rule, LastGrammarSlot] = new HashMap()

  private var reachabilityGraph: Map[HeadGrammarSlot, Set[HeadGrammarSlot]] = new HashMap()

  private var conditionSlots: List[BodyGrammarSlot] = new ArrayList()

  def this() {
    this("no-name")
  }

  def build(): Grammar = {
    removeUnusedNewNonterminals()
    for (newNonterminals <- newNonterminalsMap.values) {
      nonterminals.addAll(newNonterminals)
    }
    initializeGrammarProrperties()
    validate()
    new Grammar(this)
  }

  def validate() {
    val action = new GrammarVisitAction() {

      override def visit(slot: LastGrammarSlot) {
      }

      override def visit(slot: TerminalGrammarSlot) {
      }

      override def visit(slot: NonterminalGrammarSlot) {
        if (slot.getNonterminal.getAlternates.size == 0) {
          throw new GrammarValidationException("No alternates defined for " + slot.getNonterminal)
        }
      }

      override def visit(head: HeadGrammarSlot) {
        if (head.getAlternates.size == 0) {
          throw new GrammarValidationException("No alternates defined for " + head)
        }
      }

      override def visit(slot: KeywordGrammarSlot) {
      }
    }
    for (head <- nonterminals) {
      GrammarVisitor.visit(head, action)
    }
  }

  def addRules(rules: java.lang.Iterable[Rule]): GrammarBuilder = {
    for (rule <- rules) {
      addRule(rule)
    }
    this
  }

  def addRule(rule: Rule): GrammarBuilder = {
    if (rule == null) {
      throw new IllegalArgumentException("Rule cannot be null.")
    }
    val conditions = new HashMap[BodyGrammarSlot, java.lang.Iterable[Condition]]()
    val head = rule.head
    val body: Seq[Symbol] = rule.body
    val headGrammarSlot = getHeadGrammarSlot(head)
    var currentSlot: BodyGrammarSlot = null
    if (body.size == 0) {
      currentSlot = new EpsilonGrammarSlot(0, headGrammarSlot, rule.getObject)
      headGrammarSlot.addAlternate(new Alternate(currentSlot))
    } else {
      var symbolIndex = 0
      var firstSlot: BodyGrammarSlot = null
      for (symbol <- body) {
        currentSlot = getBodyGrammarSlot(symbol, symbolIndex, currentSlot, headGrammarSlot)
        if (symbolIndex == 0) {
          firstSlot = currentSlot
        }
        symbolIndex += 1
        conditions.put(currentSlot, symbol.getConditions)
      }
      val lastGrammarSlot = new LastGrammarSlot(symbolIndex, currentSlot, headGrammarSlot, rule.getObject)
      ruleToLastSlotMap.put(rule, lastGrammarSlot)
      val alternate = new Alternate(firstSlot)
      headGrammarSlot.addAlternate(alternate)
      for ((key, value) <- conditions; condition <- value) {
        addCondition(key, condition)
      }
    }
    this
  }

  private def getBodyGrammarSlot(symbol: Symbol, 
      symbolIndex: Int, 
      currentSlot: BodyGrammarSlot, 
      headGrammarSlot: HeadGrammarSlot): BodyGrammarSlot = {
    if (symbol.isInstanceOf[Keyword]) {
      val keyword = symbol.asInstanceOf[Keyword]
      val keywordHead = getHeadGrammarSlot(new Nonterminal(keyword.getName))
      new KeywordGrammarSlot(symbolIndex, keywordHead, symbol.asInstanceOf[Keyword], currentSlot, headGrammarSlot)
    } else if (symbol.isInstanceOf[Terminal]) {
      new TerminalGrammarSlot(symbolIndex, currentSlot, symbol.asInstanceOf[Terminal], headGrammarSlot)
    } else {
      val nonterminal = getHeadGrammarSlot(symbol.asInstanceOf[Nonterminal])
      new NonterminalGrammarSlot(symbolIndex, currentSlot, nonterminal, headGrammarSlot)
    }
  }

  private def addCondition(slot: BodyGrammarSlot, condition: Condition) = condition.getType match {
    case FOLLOW => //break
    case NOT_FOLLOW => if (condition.isInstanceOf[TerminalCondition]) {
      NotFollowActions.fromTerminalList(slot.next(), condition.asInstanceOf[TerminalCondition].getTerminals)
    } else if (condition.isInstanceOf[KeywordCondition]) {
      NotFollowActions.fromKeywordList(slot.next(), condition.asInstanceOf[KeywordCondition].getKeywords)
    } else {
      NotFollowActions.fromGrammarSlot(slot.next(), convertCondition(condition.asInstanceOf[ContextFreeCondition]))
    }
    case PRECEDE => //break
    case NOT_PRECEDE => 
      assert(!(condition.isInstanceOf[ContextFreeCondition]))
      if (condition.isInstanceOf[KeywordCondition]) {
        val literalCondition = condition.asInstanceOf[KeywordCondition]
        NotPrecedeActions.fromKeywordList(slot, literalCondition.getKeywords)
      } else {
        val terminalCondition = condition.asInstanceOf[TerminalCondition]
        NotPrecedeActions.fromTerminalList(slot, terminalCondition.getTerminals)
      }

    case MATCH => //break
    case NOT_MATCH => if (condition.isInstanceOf[ContextFreeCondition]) {
      NotMatchActions.fromGrammarSlot(slot.next(), convertCondition(condition.asInstanceOf[ContextFreeCondition]))
    } else {
      val simpleCondition = condition.asInstanceOf[KeywordCondition]
      NotMatchActions.fromKeywordList(slot.next(), simpleCondition.getKeywords)
    }
    case START_OF_LINE => LineActions.addStartOfLine(slot)
    case END_OF_LINE => LineActions.addEndOfLine(slot.next())
  }

  private def convertCondition(condition: ContextFreeCondition): BodyGrammarSlot = {
    if (condition == null) {
      return null
    }
    if (condition.getSymbols.size == 0) {
      throw new IllegalArgumentException("The list of symbols cannot be empty.")
    }
    var currentSlot: BodyGrammarSlot = null
    var firstSlot: BodyGrammarSlot = null
    var index = 0
    for (symbol <- condition.getSymbols) {
      if (symbol.isInstanceOf[Nonterminal]) {
        val nonterminal = getHeadGrammarSlot(symbol.asInstanceOf[Nonterminal])
        currentSlot = new NonterminalGrammarSlot(index, currentSlot, nonterminal, null)
      } else if (symbol.isInstanceOf[Terminal]) {
        currentSlot = new TerminalGrammarSlot(index, currentSlot, symbol.asInstanceOf[Terminal], null)
      } else if (symbol.isInstanceOf[Keyword]) {
        currentSlot = new KeywordGrammarSlot(index, getHeadGrammarSlot(new Nonterminal(symbol.getName)), 
          symbol.asInstanceOf[Keyword], currentSlot, null)
      }
      if (index == 0) {
        firstSlot = currentSlot
      }
      index += 1
    }
    new LastGrammarSlot(index, currentSlot, null, null)
    conditionSlots.add(firstSlot)
    firstSlot
  }

  private def setTestSets(slots: List[BodyGrammarSlot]) {
    for (slot <- slots) {
      var currentSlot = slot
      while (!(currentSlot.isInstanceOf[LastGrammarSlot])) {
        if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
          currentSlot.asInstanceOf[NonterminalGrammarSlot].setTestSet()
        }
        currentSlot = currentSlot.next()
      }
    }
  }

  private def getHeadGrammarSlot(nonterminal: Nonterminal): HeadGrammarSlot = {
    var headGrammarSlot = nonterminalsMap.get(nonterminal)
    if (headGrammarSlot == null) {
      headGrammarSlot = new HeadGrammarSlot(nonterminal)
      nonterminalsMap.put(nonterminal, headGrammarSlot)
      nonterminals.add(headGrammarSlot)
    }
    headGrammarSlot
  }

  private def initializeGrammarProrperties() {
    calculateLongestTerminalChain()
    calculateMaximumNumAlternates()
    calculateFirstSets()
    calculateFollowSets()
    setTestSets()
    setTestSets(conditionSlots)
    setSlotIds()
    setDirectNullables()
    calculateReachabilityGraph()
    calculateExpectedDescriptors()
  }

  private def calculateLongestTerminalChain() {
    val action = new LongestTerminalChainAction()
    GrammarVisitor.visit(nonterminals, action)
    longestTerminalChain = action.getLongestTerminalChain
  }

  private def calculateMaximumNumAlternates() {
    var max = 0
    for (head <- nonterminals if head.getCountAlternates > max) {
      max = head.getCountAlternates
    }
    this.maximumNumAlternates = max
  }

  private def calculateExpectedDescriptors() {
    val expectedDescriptors = new ArrayList[Integer]()
    for (head <- nonterminals) {
      var num = head.getCountAlternates
      val directReachableNonterminals = getDirectReachableNonterminals(head)
      for (nt <- directReachableNonterminals) {
        num += nt.getCountAlternates
      }
      val indirectReachableNonterminals = new HashSet[HeadGrammarSlot](reachabilityGraph.get(head))
      indirectReachableNonterminals.remove(directReachableNonterminals)
      for (nt <- indirectReachableNonterminals) {
        num += nt.getCountAlternates
      }
      expectedDescriptors.add(num)
    }
    averageDescriptors = 0
    maxDescriptors = 0
    for (i <- expectedDescriptors) {
      averageDescriptors += i
      if (i > maxDescriptors) {
        maxDescriptors = i
      }
    }
    averageDescriptors /= expectedDescriptors.size
    stDevDescriptors = 0
    for (i <- expectedDescriptors) {
      stDevDescriptors += Math.sqrt(Math.abs(i - averageDescriptors))
    }
    stDevDescriptors /= expectedDescriptors.size
  }

  private def getDirectReachableNonterminals(head: HeadGrammarSlot): Set[HeadGrammarSlot] = {
    val set = new HashSet[HeadGrammarSlot]()
    for (alt <- head.getAlternates if alt.getSlotAt(0).isInstanceOf[NonterminalGrammarSlot]) {
      set.add(alt.getSlotAt(0).asInstanceOf[NonterminalGrammarSlot]
        .getNonterminal)
    }
    set
  }

  private def calculateFirstSets() {
    var changed = true
    while (changed) {
      changed = false
      for (head <- nonterminals; alternate <- head.getAlternates) {
        changed |= addFirstSet(head.firstSet, alternate.getFirstSlot, changed)
      }
    }
  }

  private def addFirstSet(set: Set[Terminal], currentSlot: BodyGrammarSlot, _changed: Boolean): Boolean = {
    var changed = _changed
    if (currentSlot.isInstanceOf[EpsilonGrammarSlot]) {
      set.add(Epsilon.getInstance) || changed
    } else if (currentSlot.isInstanceOf[TerminalGrammarSlot]) {
      set.add(currentSlot.asInstanceOf[TerminalGrammarSlot].getTerminal) || 
        changed
    } else if (currentSlot.isInstanceOf[KeywordGrammarSlot]) {
      set.add(currentSlot.asInstanceOf[KeywordGrammarSlot].getKeyword
        .getFirstTerminal) || 
        changed
    } else if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
      val nonterminalGrammarSlot = currentSlot.asInstanceOf[NonterminalGrammarSlot]
      changed = set.addAll(nonterminalGrammarSlot.getNonterminal.getFirstSet) || 
        changed
      if (isNullable(nonterminalGrammarSlot.getNonterminal)) {
        return addFirstSet(set, currentSlot.next(), changed) || changed
      }
      changed
    } else {
      changed
    }
  }

  private def isNullable(nt: HeadGrammarSlot): Boolean = {
    nt.getFirstSet.contains(Epsilon.getInstance)
  }

  private def isChainNullable(slot: BodyGrammarSlot): Boolean = {
    if (!(slot.isInstanceOf[LastGrammarSlot])) {
      if (slot.isInstanceOf[TerminalGrammarSlot] || slot.isInstanceOf[KeywordGrammarSlot]) {
        return false
      }
      val ntGrammarSlot = slot.asInstanceOf[NonterminalGrammarSlot]
      return isNullable(ntGrammarSlot.getNonterminal) && isChainNullable(ntGrammarSlot.next())
    }
    true
  }

  private def calculateFollowSets() {
    var changed = true
    while (changed) {
      changed = false
      for (head <- nonterminals; alternate <- head.getAlternates) {
        var currentSlot = alternate.getFirstSlot
        while (!(currentSlot.isInstanceOf[LastGrammarSlot])) {
          if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
            val nonterminalGrammarSlot = currentSlot.asInstanceOf[NonterminalGrammarSlot]
            val next = currentSlot.next()
            if (next.isInstanceOf[LastGrammarSlot]) {
              changed |= nonterminalGrammarSlot.getNonterminal.getFollowSet.addAll(head.getFollowSet)
              //break
            }
            val followSet = nonterminalGrammarSlot.getNonterminal.getFollowSet
            changed |= addFirstSet(followSet, currentSlot.next(), changed)
            if (isChainNullable(next)) {
              changed |= nonterminalGrammarSlot.getNonterminal.getFollowSet.addAll(head.getFollowSet)
            }
          }
          currentSlot = currentSlot.next()
        }
      }
    }
    for (head <- nonterminals) {
      head.followSet - (Epsilon.getInstance)
      head.followSet + (EOF.getInstance)
    }
  }

  private def setTestSets() {
    for (head <- nonterminals) {
      val nullable = head.getFirstSet.contains(Epsilon.getInstance)
      var directNullable = false
      if (nullable) {
        for (alt <- head.getAlternates if alt.isEmpty) {
          directNullable = true
          head.setEpsilonAlternate(alt)
          //break
        }
      }
      head.setNullable(nullable, directNullable)
      for (alternate <- head.getAlternates) {
        var currentSlot = alternate.getFirstSlot
        while (!(currentSlot.isInstanceOf[LastGrammarSlot])) {
          if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
            currentSlot.asInstanceOf[NonterminalGrammarSlot].setTestSet()
          }
          currentSlot = currentSlot.next()
        }
      }
    }
  }

  private def setSlotIds() {
    slots = new ArrayList()
    for (nonterminal <- nonterminals; alternate <- nonterminal.getAlternates) {
      var currentSlot = alternate.getFirstSlot
      while (currentSlot != null) {
        slots.add(currentSlot)
        currentSlot = currentSlot.next()
      }
    }
    var i = 0
    for (head <- nonterminals) {
      head.setId(i)
      i += 1
    }
    for (slot <- slots) {
      slot.setId(i)
      i += 1
    }
    for (slot <- conditionSlots) {
      slot.setId(i)
      i += 1
    }
  }

  private def setDirectNullables() {
    for (head <- nonterminals; alternate <- head.getAlternates) {
      var currentSlot = alternate.getFirstSlot
      while (!(currentSlot.isInstanceOf[LastGrammarSlot])) {
        if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
          val ntSlot = currentSlot.asInstanceOf[NonterminalGrammarSlot]
          if (ntSlot.getNonterminal.isDirectNullable) {
            val directNullableSlot = new DirectNullableNonterminalGrammarSlot(ntSlot.getPosition, ntSlot.previous(), 
              ntSlot.getNonterminal, ntSlot.getHead)
            ntSlot.next().setPrevious(directNullableSlot)
            directNullableSlot.setNext(ntSlot.next())
            directNullableSlot.setTestSet()
            directNullableSlot.setId(ntSlot.getId)
            slots.remove(ntSlot)
            slots.add(directNullableSlot)
          }
        }
        currentSlot = currentSlot.next()
      }
    }
  }

  def rewritePatterns() {
    rewritePrecedencePatterns()
    rewriteExceptPatterns()
  }

  def rewriteExceptPatterns() {
    rewriteExceptPatterns(groupPatterns(exceptPatterns))
  }

  def rewritePrecedencePatterns() {
    for ((key, value) <- precednecePatternsMap) {
      log.debug("Applying the pattern %s with %d.", key, value.size)
      val nonterminal = nonterminalsMap.get(key)
      val groupPatterns = groupPatterns(value)
      rewriteFirstLevel(nonterminal, groupPatterns)
      rewriteDeeperLevels(nonterminal, groupPatterns)
    }
  }

  private def rewriteExceptPatterns(patterns: Map[ExceptPattern, Set[List[Symbol]]]) {
    for ((key, value) <- patterns) {
      val pattern = key
      for (alt <- nonterminalsMap.get(pattern.getNonterminal).getAlternates if alt.`match`(pattern.getParent)) {
        createNewNonterminal(alt, pattern.getPosition, value)
      }
      for (head <- newNonterminalsMap.get(pattern.getNonterminal); alt <- head.getAlternates if alt.`match`(pattern.getParent)) {
        createNewNonterminal(alt, pattern.getPosition, value)
      }
    }
  }

  private def groupPatterns[T <: AbstractPattern](patterns: java.lang.Iterable[T]): Map[T, Set[List[Symbol]]] = {
    val group = new LinkedHashMap[T, Set[List[Symbol]]]()
    for (pattern <- patterns) {
      var set = group.get(pattern)
      if (set == null) {
        set = new LinkedHashSet()
        group.put(pattern, set)
      }
      set.add(pattern.getChild)
    }
    group
  }

  private def rewriteFirstLevel(head: HeadGrammarSlot, patterns: Map[PrecedencePattern, Set[List[Symbol]]]) {
    val freshNonterminals = new LinkedHashMap[PrecedencePattern, HeadGrammarSlot]()
    val map = new HashMap[Set[List[Symbol]], HeadGrammarSlot]()
    for ((key, value) <- patterns) {
      val pattern = key
      var freshNonterminal = map.get(value)
      if (freshNonterminal == null) {
        freshNonterminal = new HeadGrammarSlot(pattern.getNonterminal)
        addNewNonterminal(freshNonterminal)
        map.put(value, freshNonterminal)
      }
      freshNonterminals.put(pattern, freshNonterminal)
    }
    for ((key, value) <- patterns; alt <- head.getAlternates) {
      val pattern = key
      if (!alt.`match`(pattern.getParent)) {
        //continue
      }
      log.trace("Applying the pattern %s on %s.", pattern, alt)
      if (!pattern.isDirect) {
        var copy: HeadGrammarSlot = null
        val alternates = new ArrayList[Alternate]()
        if (pattern.isLeftMost) {
          copy = copyIndirectAtLeft(alt.getNonterminalAt(pattern.getPosition), pattern.getNonterminal)
          getLeftEnds(copy, pattern.getNonterminal, alternates)
          for (a <- alternates) {
            a.setNonterminalAt(0, freshNonterminals.get(pattern))
          }
        } else {
          copy = copyIndirectAtRight(alt.getNonterminalAt(pattern.getPosition), pattern.getNonterminal)
          getRightEnds(copy, pattern.getNonterminal, alternates)
          for (a <- alternates) {
            a.setNonterminalAt(a.size - 1, freshNonterminals.get(pattern))
          }
        }
        alt.setNonterminalAt(pattern.getPosition, copy)
      } else {
        alt.setNonterminalAt(pattern.getPosition, freshNonterminals.get(pattern))
      }
    }
    for ((key, value) <- freshNonterminals) {
      val pattern = key
      val freshNontermianl = value
      val alternates = head.without(patterns.get(pattern))
      val copyAlternates = copyAlternates(freshNontermianl, alternates)
      freshNontermianl.setAlternates(copyAlternates)
      existingAlternates.put(new HashSet(copyAlternates), freshNontermianl)
    }
  }

  private def addNewNonterminal(nonterminal: HeadGrammarSlot) {
    var list = newNonterminalsMap.get(nonterminal.getNonterminal)
    if (list == null) {
      list = new ArrayList()
      newNonterminalsMap.put(nonterminal.getNonterminal, list)
    }
    list.add(nonterminal)
  }

  private def rewriteDeeperLevels(head: HeadGrammarSlot, patterns: Map[PrecedencePattern, Set[List[Symbol]]]) {
    for ((key, value) <- patterns) {
      val pattern = key
      val children = value
      for (alt <- head.getAlternates) {
        if (pattern.isLeftMost && alt.`match`(pattern.getParent)) {
          rewriteRightEnds(alt.getNonterminalAt(0), pattern, children)
        }
        if (pattern.isRightMost && alt.`match`(pattern.getParent)) {
          rewriteLeftEnds(alt.getNonterminalAt(alt.size - 1), pattern, children)
        }
      }
    }
  }

  private def createNewNonterminal(alt: Alternate, position: Int, filteredAlternates: Set[List[Symbol]]): HeadGrammarSlot = {
    val filteredNonterminal = alt.getNonterminalAt(position)
    var newNonterminal = existingAlternates.get(filteredNonterminal.without(filteredAlternates))
    if (newNonterminal == null) {
      newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal)
      addNewNonterminal(newNonterminal)
      alt.setNonterminalAt(position, newNonterminal)
      val copy = copyAlternates(newNonterminal, filteredNonterminal.without(filteredAlternates))
      existingAlternates.put(new HashSet(copy), newNonterminal)
      newNonterminal.setAlternates(copy)
    } else {
      alt.setNonterminalAt(position, newNonterminal)
    }
    newNonterminal
  }

  private def rewriteRightEnds(nonterminal: HeadGrammarSlot, pattern: PrecedencePattern, children: Set[List[Symbol]]) {
    if (nonterminal.getNonterminal == pattern.getNonterminal) {
      for (alternate <- nonterminal.getAlternates) {
        if (!(alternate.getLastSlot.isInstanceOf[NonterminalGrammarSlot])) {
          //continue
        }
        val last = alternate.getLastSlot.asInstanceOf[NonterminalGrammarSlot]
          .getNonterminal
        if (last.contains(children)) {
          val newNonterminal = createNewNonterminal(alternate, alternate.size - 1, children)
          rewriteRightEnds(newNonterminal, pattern, children)
        }
      }
    } else {
      assert(pattern.isLeftMost)
      val alternates = new ArrayList[Alternate]()
      getLeftEnds(nonterminal, pattern.getNonterminal, alternates)
      for (alt <- alternates) {
        rewriteRightEnds(alt.getNonterminalAt(0), pattern, children)
      }
    }
  }

  private def rewriteLeftEnds(nonterminal: HeadGrammarSlot, pattern: PrecedencePattern, children: Set[List[Symbol]]) {
    if (nonterminal.getNonterminal == pattern.getNonterminal) {
      for (alternate <- nonterminal.getAlternates) {
        if (!(alternate.getFirstSlot.isInstanceOf[NonterminalGrammarSlot])) {
          //continue
        }
        val first = alternate.getFirstSlot.asInstanceOf[NonterminalGrammarSlot]
          .getNonterminal
        if (first.contains(children)) {
          val newNonterminal = createNewNonterminal(alternate, 0, children)
          rewriteLeftEnds(newNonterminal, pattern, children)
        }
      }
    } else {
      assert(pattern.isRightMost)
      val alternates = new ArrayList[Alternate]()
      getRightEnds(nonterminal, pattern.getNonterminal, alternates)
      for (alt <- alternates) {
        rewriteLeftEnds(alt.getNonterminalAt(alt.size - 1), pattern, children)
      }
    }
  }

  private def getRightEnds(head: HeadGrammarSlot, directNonterminal: Nonterminal, alternates: List[Alternate]) {
    getRightEnds(head, directNonterminal, alternates, new HashSet[HeadGrammarSlot]())
  }

  private def getRightEnds(head: HeadGrammarSlot, 
      directNonterminal: Nonterminal, 
      alternates: List[Alternate], 
      visited: Set[HeadGrammarSlot]) {
    if (visited.contains(head)) {
      return
    }
    for (alt <- head.getAlternates if alt.getLastSlot.isInstanceOf[NonterminalGrammarSlot]) {
      val last = alt.getLastSlot.asInstanceOf[NonterminalGrammarSlot]
        .getNonterminal
      if (last.getNonterminal == directNonterminal) {
        alternates.add(alt)
      } else {
        visited.add(last)
        getRightEnds(last, directNonterminal, alternates, visited)
      }
    }
  }

  private def getLeftEnds(head: HeadGrammarSlot, nonterminal: Nonterminal, nonterminals: List[Alternate]) {
    getLeftEnds(head, nonterminal, nonterminals, new HashSet[HeadGrammarSlot]())
  }

  private def getLeftEnds(head: HeadGrammarSlot, 
      nonterminal: Nonterminal, 
      nonterminals: List[Alternate], 
      visited: Set[HeadGrammarSlot]) {
    if (visited.contains(head)) {
      return
    }
    for (alt <- head.getAlternates if alt.getFirstSlot.isInstanceOf[NonterminalGrammarSlot]) {
      val first = alt.getFirstSlot.asInstanceOf[NonterminalGrammarSlot]
        .getNonterminal
      if (first.getNonterminal == nonterminal) {
        nonterminals.add(alt)
      } else {
        visited.add(first)
        getLeftEnds(first, nonterminal, nonterminals, visited)
      }
    }
  }

  private def copyIndirectAtLeft(head: HeadGrammarSlot, directNonterminal: Nonterminal): HeadGrammarSlot = {
    copyIndirectAtLeft(head, directNonterminal, new HashMap[HeadGrammarSlot, HeadGrammarSlot]())
  }

  private def copyIndirectAtRight(head: HeadGrammarSlot, directNonterminal: Nonterminal): HeadGrammarSlot = {
    copyIndirectAtRight(head, directNonterminal, new HashMap[HeadGrammarSlot, HeadGrammarSlot]())
  }

  private def copyIndirectAtLeft(head: HeadGrammarSlot, directName: Nonterminal, map: HashMap[HeadGrammarSlot, HeadGrammarSlot]): HeadGrammarSlot = {
    var copy = map.get(head)
    if (copy != null) {
      return copy
    }
    copy = new HeadGrammarSlot(head.getNonterminal)
    addNewNonterminal(copy)
    map.put(head, copy)
    val copyAlternates = copyAlternates(copy, head.getAlternates)
    copy.setAlternates(copyAlternates)
    for (alt <- copyAlternates if alt.getSlotAt(0).isInstanceOf[NonterminalGrammarSlot]) {
      val nonterminal = alt.getSlotAt(0).asInstanceOf[NonterminalGrammarSlot]
        .getNonterminal
      if (nonterminal.getNonterminal != directName) {
        alt.setNonterminalAt(0, copyIndirectAtLeft(nonterminal, directName, map))
      }
    }
    copy
  }

  private def copyIndirectAtRight(head: HeadGrammarSlot, directNonterminal: Nonterminal, map: HashMap[HeadGrammarSlot, HeadGrammarSlot]): HeadGrammarSlot = {
    var copy = map.get(head)
    if (copy != null) {
      return copy
    }
    copy = new HeadGrammarSlot(head.getNonterminal)
    addNewNonterminal(copy)
    map.put(head, copy)
    val copyAlternates = doCopyAlternates(copy, head.getAlternates)
    copy.setAlternates(copyAlternates)
    for (alt <- copyAlternates if alt.getSlotAt(alt.size - 1).isInstanceOf[NonterminalGrammarSlot]) {
      val nonterminal = alt.getSlotAt(alt.size - 1).asInstanceOf[NonterminalGrammarSlot]
        .getNonterminal
      if (nonterminal.getNonterminal != directNonterminal) {
        alt.setNonterminalAt(alt.size - 1, copyIndirectAtLeft(nonterminal, directNonterminal, map))
      }
    }
    copy
  }

  private def doCopyAlternates(head: HeadGrammarSlot, list: java.lang.Iterable[Alternate]): List[Alternate] = {
    val copyList = new ArrayList[Alternate]()
    for (alt <- list) {
      copyList.add(copyAlternate(alt, head))
    }
    copyList
  }

  private def copyAlternate(alternate: Alternate, head: HeadGrammarSlot): Alternate = {
    val copyFirstSlot = copySlot(alternate.getFirstSlot, null, head)
    var current = alternate.getFirstSlot.next()
    var copy = copyFirstSlot
    while (current != null) {
      copy = copySlot(current, copy, head)
      current = current.next()
    }
    new Alternate(copyFirstSlot)
  }

  private def copySlot(slot: BodyGrammarSlot, previous: BodyGrammarSlot, head: HeadGrammarSlot): BodyGrammarSlot = {
    var copy: BodyGrammarSlot = null
    if (slot.isInstanceOf[LastGrammarSlot]) {
      copy = slot.asInstanceOf[LastGrammarSlot].copy(previous, head)
    } else if (slot.isInstanceOf[NonterminalGrammarSlot]) {
      val ntSlot = slot.asInstanceOf[NonterminalGrammarSlot]
      copy = ntSlot.copy(previous, ntSlot.getNonterminal, head)
    } else if (slot.isInstanceOf[TerminalGrammarSlot]) {
      copy = slot.asInstanceOf[TerminalGrammarSlot].copy(previous, head)
    } else {
      val keyword = slot.asInstanceOf[KeywordGrammarSlot].getKeyword
      copy = slot.asInstanceOf[KeywordGrammarSlot].copy(getHeadGrammarSlot(new Nonterminal(keyword.getName)), 
        previous, head)
    }
    copy
  }

  def addPrecedencePattern(nonterminal: Nonterminal, 
      parent: Rule, 
      position: Int, 
      child: Rule) {
    val pattern = new PrecedencePattern(nonterminal, parent.body, position, child.body)
    if (precednecePatternsMap.containsKey(nonterminal)) {
      precednecePatternsMap.get(nonterminal).add(pattern)
    } else {
      val set = new ArrayList[PrecedencePattern]()
      set.add(pattern)
      precednecePatternsMap.put(nonterminal, set)
    }
    log.debug("Precedence pattern added %s", pattern)
  }

  def addExceptPattern(nonterminal: Nonterminal, 
      parent: Rule, 
      position: Int, 
      child: Rule) {
    val pattern = new ExceptPattern(nonterminal, parent.getBody, position, child.getBody)
    exceptPatterns.add(pattern)
    log.debug("Except pattern added %s", pattern)
  }

  def getReachableNonterminals(name: String): Set[HeadGrammarSlot] = {
    reachabilityGraph.get(nonterminalsMap.get(new Nonterminal(name)))
  }

  private def calculateReachabilityGraph() {
    var changed = true
    val allNonterminals = new ArrayList[HeadGrammarSlot](nonterminals)
    for (newNonterminals <- newNonterminalsMap.values) {
      allNonterminals.addAll(newNonterminals)
    }
    while (changed) {
      changed = false
      for (head <- allNonterminals) {
        var set = reachabilityGraph.get(head)
        if (set == null) {
          set = new HashSet()
        }
        reachabilityGraph.put(head, set)
        for (alternate <- head.getAlternates) {
          changed |= calculateReachabilityGraph(set, alternate.getFirstSlot, changed)
        }
      }
    }
  }

  private def calculateReachabilityGraph(set: Set[HeadGrammarSlot], currentSlot: BodyGrammarSlot, _changed: Boolean): Boolean = {
    var changed = _changed
    if (currentSlot.isInstanceOf[EpsilonGrammarSlot]) {
      false
    } else if (currentSlot.isInstanceOf[TerminalGrammarSlot]) {
      false
    } else if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
      val nonterminalGrammarSlot = currentSlot.asInstanceOf[NonterminalGrammarSlot]
      changed = set.add(nonterminalGrammarSlot.getNonterminal) || changed
      var set2 = reachabilityGraph.get(nonterminalGrammarSlot.getNonterminal)
      if (set2 == null) {
        set2 = new HashSet()
      }
      reachabilityGraph.put(nonterminalGrammarSlot.getNonterminal, set2)
      changed = set.addAll(set2) || changed
      if (isNullable(nonterminalGrammarSlot.getNonterminal)) {
        return calculateReachabilityGraph(set, currentSlot.next(), changed) || 
          changed
      }
      changed
    } else {
      changed
    }
  }

  def removeUnusedNonterminals(nonterminal: Nonterminal): GrammarBuilder = {
    val referedNonterminals = new HashSet[HeadGrammarSlot]()
    val queue = new ArrayDeque[HeadGrammarSlot]()
    queue.add(nonterminalsMap.get(nonterminal))
    while (!queue.isEmpty) {
      val head = queue.poll()
      referedNonterminals.add(head)
      for (alternate <- head.getAlternates) {
        var currentSlot = alternate.getFirstSlot
        while (currentSlot.next() != null) {
          if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
            if (!referedNonterminals.contains(currentSlot.asInstanceOf[NonterminalGrammarSlot].getNonterminal)) {
              queue.add(currentSlot.asInstanceOf[NonterminalGrammarSlot].getNonterminal)
            }
          }
          currentSlot = currentSlot.next()
        }
      }
    }
    nonterminals.retainAll(referedNonterminals)
    this
  }

  private def removeUnusedNewNonterminals() {
    val reachableNonterminals = new HashSet[HeadGrammarSlot]()
    val queue = new ArrayDeque[HeadGrammarSlot]()
    for (nonterminal <- newNonterminalsMap.keySet) {
      queue.add(nonterminalsMap.get(nonterminal))
    }
    while (!queue.isEmpty) {
      val head = queue.poll()
      reachableNonterminals.add(head)
      for (alternate <- head.getAlternates) {
        var currentSlot = alternate.getFirstSlot
        while (currentSlot.next() != null) {
          if (currentSlot.isInstanceOf[NonterminalGrammarSlot]) {
            val reachableHead = currentSlot.asInstanceOf[NonterminalGrammarSlot].getNonterminal
            if (!reachableNonterminals.contains(reachableHead)) {
              queue.add(reachableHead)
            }
          }
          currentSlot = currentSlot.next()
        }
      }
    }
    for (list <- newNonterminalsMap.values) {
      list.retainAll(reachableNonterminals)
    }
  }

  def leftFactorize(nonterminalName: String) {
    val head = nonterminalsMap.get(new Nonterminal(nonterminalName))
    val trie = new Trie[Symbol]()
    for (alt <- head.getAlternates) {
      trie.add(alt.getSymbols, alt)
    }
    val node = trie.getRoot
    head.removeAllAlternates()
    for (edge <- node.getEdges) {
      val symbolIndex = 0
      var firstSlot: BodyGrammarSlot = null
      val currentSlot = getBodyGrammarSlot(edge.getLabel, symbolIndex, null, head)
      if (symbolIndex == 0) {
        firstSlot = currentSlot
      }
      test(currentSlot, edge.getDestination, symbolIndex, head)
      val alternate = new Alternate(firstSlot)
      head.addAlternate(alternate)
    }
  }

  private var count: Int = _

  private def test(slot: BodyGrammarSlot, 
      node: Node[Symbol], 
      _symbolIndex: Int,
      headGrammarSlot: HeadGrammarSlot) {
    var symbolIndex = _symbolIndex
    if (node.size == 0) {
      new LastGrammarSlot(symbolIndex, slot, headGrammarSlot, null)
      return
    }
    if (node.size == 1) {
      val currentSlot = getBodyGrammarSlot(node.getEdges.get(0).getLabel, symbolIndex, slot, headGrammarSlot)
      test(currentSlot, node.getEdges.get(0).getDestination, symbolIndex + 1, headGrammarSlot)
      return
    }
    val nonterminal = new Nonterminal("C_" + count)
    nonterminal.setCollapsible(true)
    val newHead = new HeadGrammarSlot(nonterminal)
    nonterminalsMap.put(nonterminal, newHead)
    nonterminals.add(newHead)
    val ntSlot = new NonterminalGrammarSlot(symbolIndex + 1, slot, newHead, headGrammarSlot)
    new LastGrammarSlot(symbolIndex + 2, ntSlot, headGrammarSlot, null)
    symbolIndex = 0
    var firstSlot: BodyGrammarSlot = null
    for (edge <- node.getEdges) {
      val currentSlot = getBodyGrammarSlot(edge.getLabel, symbolIndex, null, newHead)
      if (symbolIndex == 0) {
        firstSlot = currentSlot
      }
      test(currentSlot, edge.getDestination, symbolIndex, newHead)
      val alternate = new Alternate(firstSlot)
      newHead.addAlternate(alternate)
    }
  }
}