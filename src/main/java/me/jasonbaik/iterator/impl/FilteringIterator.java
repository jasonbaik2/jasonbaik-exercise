package me.jasonbaik.iterator.impl;

import java.util.Iterator;

import me.jasonbaik.function.IObjectTest;

public class FilteringIterator<E extends Object> implements Iterator<E> {

	private final IObjectTest predicate;
	private final Iterator<E> innerIterator;

	private boolean nextExists;
	private E next;
	private E nextCandidate;

	public FilteringIterator(Iterator<E> innerIterator, IObjectTest predicate) {
		if (null == innerIterator) {
			throw new NullPointerException();
		}

		if (null == predicate) {
			throw new NullPointerException();
		}

		this.innerIterator = innerIterator;
		this.predicate = predicate;
	}

	@Override
	public boolean hasNext() {
		if (nextExists) {
			return true;
		}

		while (innerIterator.hasNext()) {
			nextCandidate = innerIterator.next();

			if (predicate.test(nextCandidate)) {
				next = nextCandidate;
				nextExists = true;
				break;
			}
		}

		return nextExists;
	}

	@Override
	public E next() {
		if (hasNext()) {
			nextExists = false;
			return next;
		}
		throw new IllegalStateException();
	}

	public IObjectTest getPredicate() {
		return predicate;
	}

	public Iterator<E> getInnerIterator() {
		return innerIterator;
	}

}