package me.jasonbaik.enumerator.impl;

import java.util.Iterator;

import me.jasonbaik.enumerator.IEnumerator;
import me.jasonbaik.function.IObjectTest;

public class FilteringEnumerator<E> implements IEnumerator<E> {

	private IEnumerator<E> enumerator;
	private IObjectTest test;

	public FilteringEnumerator(IEnumerator<E> enumerator, IObjectTest test) {
		if (null == enumerator) {
			throw new IllegalArgumentException();
		}

		if (null == test) {
			throw new IllegalArgumentException();
		}

		this.enumerator = enumerator;
		this.test = test;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			private Iterator<E> innerIterator = enumerator.iterator();
			private E next;
			private E nextCandidate;

			@Override
			public boolean hasNext() {
				if (null != next) {
					return true;
				}

				while (innerIterator.hasNext()) {
					nextCandidate = innerIterator.next();

					if (test.test(nextCandidate)) {
						next = nextCandidate;
						break;
					}
				}

				return next != null;
			}

			@Override
			public E next() {
				if (hasNext()) {
					E temp = next;
					next = null;
					return temp;
				}
				throw new IllegalStateException();
			}

		};
	}

	public IEnumerator<E> getEnumerator() {
		return enumerator;
	}

	public void setEnumerator(IEnumerator<E> enumerator) {
		this.enumerator = enumerator;
	}

	public IObjectTest getTest() {
		return test;
	}

	public void setTest(IObjectTest test) {
		this.test = test;
	}

}