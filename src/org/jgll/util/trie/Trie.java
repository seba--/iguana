package org.jgll.util.trie;

import java.util.Iterator;

public class Trie<T> {

	private Node<T> root;
	
	public Trie() {
		root = new Node<>();
	}
	
	public void add(T label) {
		add(root, label, null);
	}
	
	public Node<T> get(Iterable<T> prefix) {
		Node<T> node = root;
		
		for(T label : prefix) {
			node = getNodeWithEdgeLabel(node, label);
			if(node == null) {
				return null;
			}
		}
		
		return node;
	}
	
	private Node<T> add(Node<T> node, T label, Object object) {
		if(node.size() == 0) {
			return insert(node, label);
		}
		
		Node<T> dest = getNodeWithEdgeLabel(node, label);
		if(dest == null) {
			return insert(node, label);
		} else {
			node.addInfo(object);
			return dest;
		}
	}
	
	private Node<T> getNodeWithEdgeLabel(Node<T> node, T label) {
		for(Edge<T> edge : node.getEdges()) {
			if(edge.getLabel().equals(label)) {
				return edge.getDestination();
			}
		}
		return null;
	}
	
	public void add(Iterable<T> labels) {
		add(labels, null);
	}
	
	public void add(Iterable<T> labels, Object object) {
	
		Node<T> node = root;
		
		Iterator<T> it = labels.iterator();
		
		while(it.hasNext()) {
			T label = it.next();
			node = add(node, label, object);
		}
	}
	
	private Node<T> insert(Node<T> node, T label) {
		Node<T> newNode = new Node<>();
		node.addChild(new Edge<T>(label, newNode));
		return newNode;
	}
	
	
	public Node<T> getRoot() {
		return root;
	}
	
	
}
