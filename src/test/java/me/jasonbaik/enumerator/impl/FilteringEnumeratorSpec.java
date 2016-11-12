package me.jasonbaik.enumerator.impl;

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

import me.jasonbaik.enumerator.IEnumerator;
import me.jasonbaik.function.IObjectTest;

public class FilteringEnumeratorSpec {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private FilteringEnumerator<String> filteringEnumerator;

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
	public void whenNoIEnumeratorPassedAtConstructionThenThrowIllegalArgumentException() {
		exceptionRule.expect(IllegalArgumentException.class);

		new FilteringEnumerator<Void>(null, (o) -> {
			return false;
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void whenNoIObjectTestPassedAtConstructionThenThrowIllegalArgumentException() {
		exceptionRule.expect(IllegalArgumentException.class);

		new FilteringEnumerator<Void>(Mockito.mock(IEnumerator.class), null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void whenConstructedThenHasIEnumeratorAndIObjectTest() {
		filteringEnumerator = new FilteringEnumerator<>(Mockito.mock(FilteringEnumerator.class), Mockito.mock(IObjectTest.class));

		assertThat(filteringEnumerator.getEnumerator(), Matchers.notNullValue());
		assertThat(filteringEnumerator.getPredicate(), Matchers.notNullValue());
		assertThat(filteringEnumerator.getEnumerator(), Matchers.isA(IEnumerator.class));
		assertThat(filteringEnumerator.getPredicate(), Matchers.isA(IObjectTest.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void isIterable() {
		IEnumerator<String> enumerator = Mockito.mock(IEnumerator.class);
		Mockito.when(enumerator.iterator()).thenReturn(new Iterator<String>() {

			@Override
			public String next() {
				return null;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

		});

		filteringEnumerator = new FilteringEnumerator<>(enumerator, Mockito.mock(IObjectTest.class));
		assertThat(filteringEnumerator, Matchers.isA(Iterable.class));
		assertThat(filteringEnumerator.iterator(), Matchers.notNullValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void givenEmptyEnumeratorWhenIteratingThenNextAndTestNotCalled() {
		IEnumerator<String> enumerator = Mockito.mock(IEnumerator.class);
		Mockito.when(enumerator.iterator()).thenReturn(new Iterator<String>() {

			@Override
			public String next() {
				assert false;
				return null;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

		});

		IObjectTest test = Mockito.mock(IObjectTest.class);
		filteringEnumerator = new FilteringEnumerator<>(enumerator, test);

		for (@SuppressWarnings("unused")
		String s : filteringEnumerator) {
			assert false;
		}

		Mockito.verifyZeroInteractions(test);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void whenCallingHasNextWithNoMoreFilterPassingElementThenReturnFalse() {
		IEnumerator<String> enumerator = Mockito.mock(IEnumerator.class);
		Mockito.when(enumerator.iterator()).thenReturn(new Iterator<String>() {

			private int index = 0;

			@Override
			public String next() {
				return "";
			}

			@Override
			public boolean hasNext() {
				return index++ < 5;
			}

		});

		IObjectTest test = (o) -> {
			return false;
		};

		filteringEnumerator = new FilteringEnumerator<>(enumerator, test);
		Assert.assertFalse(filteringEnumerator.iterator().hasNext());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void whenCallingNextWithNoMoreFilterPassingElementThenThrowIllegalStateException() {
		IEnumerator<String> enumerator = Mockito.mock(IEnumerator.class);
		Mockito.when(enumerator.iterator()).thenReturn(new Iterator<String>() {

			private int index = 0;

			@Override
			public String next() {
				return "";
			}

			@Override
			public boolean hasNext() {
				return index++ < 5;
			}

		});

		IObjectTest test = (o) -> {
			return false;
		};

		filteringEnumerator = new FilteringEnumerator<>(enumerator, test);
		exceptionRule.expect(IllegalStateException.class);
		filteringEnumerator.iterator().next();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void givenNonEmptyEnumeratorWithFiletrableElementsWhenItearatingThenSkipFilterableElements() {
		IEnumerator<String> enumerator = Mockito.mock(IEnumerator.class);
		Mockito.when(enumerator.iterator()).thenReturn(new Iterator<String>() {

			private String[] array = new String[] { "apple", "banana", "apple", "pineapple", "banana" };
			private int index = 0;

			@Override
			public String next() {
				return array[index++];
			}

			@Override
			public boolean hasNext() {
				return index < array.length;
			}

		});

		IObjectTest test = (o) -> {
			if (null != o && ((String) o).indexOf("apple") != -1) {
				return false;
			}
			return true;
		};

		filteringEnumerator = new FilteringEnumerator<>(enumerator, test);

		List<String> iterated = new ArrayList<>(8);

		for (String s : filteringEnumerator) {
			iterated.add(s);
		}

		assertThat(iterated, Matchers.not(Matchers.contains("apple")));
		assertThat(iterated, Matchers.not(Matchers.contains("pineapple")));
		assertThat(String.join(",", iterated), Matchers.equalTo("banana,banana"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void whenItearatingNullElementsThenNoNullPointerException() {
		IEnumerator<String> enumerator = Mockito.mock(IEnumerator.class);
		Mockito.when(enumerator.iterator()).thenReturn(new Iterator<String>() {

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

		});

		IObjectTest test = (o) -> {
			return true;
		};

		filteringEnumerator = new FilteringEnumerator<>(enumerator, test);

		List<String> iterated = new ArrayList<>(8);

		for (String s : filteringEnumerator) {
			iterated.add(s);
		}

		assertThat(iterated, Matchers.hasSize(5));
		assertThat(String.join(",", iterated), Matchers.equalTo("null,null,null,null,null"));
	}

}
