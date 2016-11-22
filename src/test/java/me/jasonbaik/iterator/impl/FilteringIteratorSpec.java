package me.jasonbaik.iterator.impl;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import me.jasonbaik.function.IObjectTest;

public class FilteringIteratorSpec {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private FilteringIterator<String> filteringIterator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void whenNoIteratorPassedAtConstructionThenThrowNullPointerException() {
		exceptionRule.expect(NullPointerException.class);

		new FilteringIterator<Void>(null, (o) -> {
			return false;
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void whenNoIObjectTestPassedAtConstructionThenThrowNullPointerException() {
		exceptionRule.expect(NullPointerException.class);

		new FilteringIterator<Void>(Mockito.mock(Iterator.class), null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void whenConstructedThenHasIteratorAndIObjectTest() {
		filteringIterator = new FilteringIterator<>(Mockito.mock(FilteringIterator.class), Mockito.mock(IObjectTest.class));

		assertThat(filteringIterator.getInnerIterator(), Matchers.notNullValue());
		assertThat(filteringIterator.getPredicate(), Matchers.notNullValue());
		assertThat(filteringIterator.getInnerIterator(), Matchers.isA(Iterator.class));
		assertThat(filteringIterator.getPredicate(), Matchers.isA(IObjectTest.class));
	}

	@Test
	public void givenEmptyIteratorWhenIteratingThenNextAndPredicateNotCalled() {
		Iterator<String> iterator = new Iterator<String>() {

			@Override
			public String next() {
				assert false;
				return null;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

		};

		IObjectTest test = Mockito.mock(IObjectTest.class);
		filteringIterator = new FilteringIterator<>(iterator, test);

		for (; filteringIterator.hasNext();) {
			assert false;
		}

		Mockito.verifyZeroInteractions(test);
	}

	@Test
	public void whenCallingHasNextWithNoMoreFilterPassingElementThenReturnFalse() {
		Iterator<String> iterator = new Iterator<String>() {

			private String[] array = new String[] { "kiwi", "banana", "apple", "pineapple", "banana" };
			private int index = 0;

			@Override
			public String next() {
				return array[index++];
			}

			@Override
			public boolean hasNext() {
				return index < array.length;
			}

		};

		IObjectTest test = (o) -> {
			if ("kiwi".equals(o)) {
				return true;
			}
			return false;
		};

		filteringIterator = new FilteringIterator<>(iterator, test);
		Assert.assertTrue(filteringIterator.hasNext());
		filteringIterator.next();
		Assert.assertFalse(filteringIterator.hasNext());
	}

	@Test
	public void whenCallingNextWithNoMoreFilterPassingElementThenThrowIllegalStateException() {
		Iterator<String> iterator = new Iterator<String>() {

			private String[] array = new String[] { "kiwi", "banana", "apple", "pineapple", "banana" };
			private int index = 0;

			@Override
			public String next() {
				return array[index++];
			}

			@Override
			public boolean hasNext() {
				return index < array.length;
			}

		};

		IObjectTest test = (o) -> {
			if ("kiwi".equals(o)) {
				return true;
			}
			return false;
		};

		filteringIterator = new FilteringIterator<>(iterator, test);
		assertThat(filteringIterator.next(), Matchers.equalTo("kiwi"));
		exceptionRule.expect(IllegalStateException.class);
		filteringIterator.next();
	}

	@Test
	public void givenNonEmptyIteratorWithFilterableElementsWhenItearatingThenSkipFilterableElements() {
		Iterator<String> iterator = new Iterator<String>() {

			private String[] array = new String[] { "kiwi", "banana", "apple", "pineapple", "banana" };
			private int index = 0;

			@Override
			public String next() {
				return array[index++];
			}

			@Override
			public boolean hasNext() {
				return index < array.length;
			}

		};

		IObjectTest test = (o) -> {
			if (null != o && ((String) o).indexOf("apple") != -1) {
				return false;
			}
			return true;
		};

		filteringIterator = new FilteringIterator<>(iterator, test);

		List<String> iterated = new ArrayList<>(8);

		for (; filteringIterator.hasNext();) {
			iterated.add(filteringIterator.next());
		}

		assertThat(iterated, Matchers.not(Matchers.contains("apple")));
		assertThat(iterated, Matchers.not(Matchers.contains("pineapple")));
		assertThat(String.join(",", iterated), Matchers.equalTo("kiwi,banana,banana"));
	}

	@Test
	public void whenItearatingNullElementsThenNoNullPointerException() {
		Iterator<String> iterator = new Iterator<String>() {

			private String[] array = new String[] { null, null, null, null, null };
			private int index = 0;

			@Override
			public String next() {
				return array[index++];
			}

			@Override
			public boolean hasNext() {
				return index < array.length;
			}

		};

		IObjectTest test = (o) -> {
			return true;
		};

		filteringIterator = new FilteringIterator<>(iterator, test);

		List<String> iterated = new ArrayList<>(8);

		for (; filteringIterator.hasNext();) {
			iterated.add(filteringIterator.next());
		}

		assertThat(iterated, Matchers.hasSize(5));
		assertThat(String.join(",", iterated), Matchers.equalTo("null,null,null,null,null"));
	}

}