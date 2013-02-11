package org.jgll.sppf;

import java.util.Iterator;

import org.jgll.traversal.SPPFVisitor;

/**
 * An SPPF node is a node in an Shared Packed Parse Forest. This data structure
 * is used to store the parse forest that is the result of parsing an input
 * string with a GLL parser. <br />
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */

public abstract class SPPFNode implements Iterable<SPPFNode> {

	private boolean visited;

	private Object object;

	public abstract String getId();

	public abstract String getLabel();

	public abstract SPPFNode get(int index);

	public abstract int size();

	public abstract int getLeftExtent();

	public abstract int getRightExtent();

	public abstract void accept(SPPFVisitor visitAction);

	public Iterable<Object> childrenValues() {

		final Iterator<SPPFNode> iterator = iterator();

		return new Iterable<Object>() {

			@Override
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {

					private SPPFNode next;

					@Override
					public boolean hasNext() {
						while (iterator.hasNext() && (next = iterator.next()).getObject() != null) {
							return true;
						}
						return false;
					}

					@Override
					public Object next() {
						return next.getObject();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};

			}
		};
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}
