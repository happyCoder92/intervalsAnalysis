package ch.ethz.sae;

import static org.junit.Assert.*;

import org.junit.Test;
import static ch.ethz.sae.IntervalHelper.*;

public class IntervalTest {

	@Test(expected=IllegalArgumentException.class)
	public void testConstructor() {
		@SuppressWarnings("unused")
		Interval i = i(1, 0);
	}

	@Test
	public void testIsTop() {
		assertFalse(i(-1, 0).isTop());
		assertFalse(i(mi, 0).isTop());
		assertFalse(i(-1, ma).isTop());
		assertFalse(i().isTop());
		assertTrue(i(mi, ma).isTop());
	}
	
	@Test
	public void testIsBottom() {
		assertFalse(i(-1, 0).isBottom());
		assertFalse(i(mi, ma).isBottom());
		assertTrue(i().isBottom());
	}
	
	@Test
	public void testEquals() {
		assertFalse(i().equals(new Object()));
		assertFalse(i().equals(i(0, 1)));
		assertFalse(i(0, 1).equals(i()));
		assertFalse(i(0, 2).equals(i(0, 1)));
		assertTrue(i().equals(i()));
		assertTrue(i(0, 1).equals(i(0, 1)));
	}
	
	@Test
	public void testWiden() {
		assertEquals(i(0, 2), Interval.widen(i(0, 2), i(0, 1)));
		assertEquals(i(0, 2), Interval.widen(i(0, 2), i()));
		assertEquals(i(0, 2), Interval.widen(i(), i(0, 2)));
		assertEquals(i(0, ma), Interval.widen(i(0, 2), i(0, 3)));
		assertEquals(i(mi, 2), Interval.widen(i(0, 2), i(-1, 1)));
		assertEquals(i(mi, ma), Interval.widen(i(0, 2), i(-1, 3)));
	}
	
	@Test
	public void testSize() {
		assertEquals(0, i().size());
		assertEquals(10, i(0, 9).size());
		assertEquals((long)2*(-(long)mi), i(mi, ma).size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMinus() {
		@SuppressWarnings("unused")
		Interval i = Interval.minus(i());
	}
	
	@Test
	public void testBottom() {
		assertEquals(i(), Interval.bottom());
		assertTrue(Interval.bottom().isBottom());
	}
	
	@Test
	public void testTop() {
		assertEquals(i(mi, ma), Interval.top());
		assertTrue(Interval.top().isTop());
	}
	
	@Test
	public void testToString() {
		assertEquals("[0,1]", i(0, 1).toString());
	}
}
