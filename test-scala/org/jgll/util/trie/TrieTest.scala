package org.jgll.util.trie

import org.junit.Test
import org.jgll.util.CollectionsUtil._
import org.junit.Assert._
//remove if not needed
import scala.collection.JavaConversions._

class TrieTest {

  @Test
  def test1() {
    val trie = new Trie[String]()
    trie.add("du")
    trie.add(list("du", "hast"))
    trie.add(list("du", "hast", "mich", "gefragt"))
    trie.add(list("und", "ich", "hab"))
    trie.add(list("und", "ich", "hab", "nichts", "gesagt"))
  }

  @Test
  def test2() {
    val trie = new Trie[String]()
    trie.add(list("E", "*", "E"))
    trie.add(list("E", "+", "E"))
    trie.add(list("E", "-", "E"))
    trie.add(list("-", "E"))
    val node = trie.get(list("E"))
    assertEquals(3, node.size)
  }
}
