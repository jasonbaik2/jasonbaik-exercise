package me.jasonbaik.enumerator.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import me.jasonbaik.enumerator.IEnumerator;

public class FilteringEnumeratorSpec {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private FilteringEnumerator enumerator;

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
	public void whenNoIEnumeratorAtConstructionThenThrowIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);

		new FilteringEnumerator(null, (o) -> {
			return false;
		});
	}

	@Test
	public void whenNoIObjectTestAtConstructionThenThrowIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);

		new FilteringEnumerator(Mockito.mock(IEnumerator.class), null);
	}

}
