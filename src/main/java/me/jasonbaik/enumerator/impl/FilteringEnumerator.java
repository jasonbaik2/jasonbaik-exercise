package me.jasonbaik.enumerator.impl;

import java.util.Iterator;

import me.jasonbaik.enumerator.IEnumerator;
import me.jasonbaik.function.IObjectTest;

public class FilteringEnumerator<E extends Object> implements IEnumerator<E> {

	private final IEnumerator<E> enumerator;
	private final IObjectTest predicate;

	public FilteringEnumerator(IEnumerator<E> enumerator, IObjectTest predicate) {
		if (null == enumerator) {
			throw new IllegalArgumentException();
		}

		if (null == predicate) {
			throw new IllegalArgumentException();
		}

		this.enumerator = enumerator;
		this.predicate = predicate;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			private Iterator<E> innerIterator = enumerator.iterator();
			private boolean nextExists;
			private E next;
			private E nextCandidate;

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

		};
	}

	public IEnumerator<E> getEnumerator() {
		return enumerator;
	}

	public IObjectTest getPredicate() {
		return predicate;
	}

}