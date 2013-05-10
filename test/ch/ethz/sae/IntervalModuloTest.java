package ch.ethz.sae;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalModuloTest {
	private final Interval a, b;
	private final Interval expected;

	public IntervalModuloTest(Interval a, Interval b, Interval expected) {
		this.a = a;
		this.b = b;
		this.expected = expected;
	}
	
	public static Interval i(int a, int b) {
		return new Interval(a, b);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		@SuppressWarnings("unused")
		final int ma = Integer.MAX_VALUE;
		@SuppressWarnings("unused")
		final int mi = Integer.MIN_VALUE;
		return Arrays.asList(new Interval[][] {
				{ i(10, 10), i(5, 5), i(0, 0) }, // point cases
				{ i(10, 10), i(-5, -5), i(0, 0) }, // point cases
				{ i(10, 10), i(3, 3), i(1, 1) }, // .
				{ i(10, 10), i(-3, -3), i(1, 1) }, // .
				{ i(8, 10), i(3, 3), i(0, 2) }, // range-point cases
				{ i(7, 10), i(3, 3), i(0, 2) }, // .
				{ i(6, 10), i(3, 3), i(0, 2) }, // .
				{ i(-2, 2), i(3, 3), i(-2, 2) }, // .
				{ i(10, 10), i(1, 5), i(0, 2) }, // point-range cases
				});
	}

	@Test
	public void test() {
		final Interval result = Interval.modulo(a, b);
		assertEquals(expected, result);
	}
}
