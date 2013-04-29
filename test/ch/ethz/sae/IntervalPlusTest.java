package ch.ethz.sae;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalPlusTest {

	private final Interval a, b;
	private final Interval expected;

	public IntervalPlusTest(Interval a, Interval b, Interval expected) {
		this.a = a;
		this.b = b;
		this.expected = expected;
	}
	
	public static Interval i(int a, int b) {
		return new Interval(a, b);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		final int ma = Integer.MAX_VALUE;
		final int mi = Integer.MIN_VALUE;
		return Arrays.asList(new Interval[][] {
				{ i(10, 10), i(20, 20), i(30, 30) }, // point case
				{ i(0, 10), i(0, 10), i(0, 20) }, // ranges cases
				{ i(0, 10), i(0, 20), i(0, 30) }, // .
				{ i(0, 20), i(-10, 10), i(-10, 30) }, // .
				{ i(ma, ma), i(1, 1), i(mi, mi) }, // right overflow at point
				{ i(mi, mi), i(-1, -1), i(ma, ma) }, // left overflow at point
				{ i(ma-1, ma), i(1, 10), i(mi, ma) }, // right range overflow
				{ i(mi, mi+1), i(-1, 10), i(mi, ma) }, // left range overflow
				{ i(mi, 1), i(-2, -2), i(mi, ma) }, // left only lower overflow
				{ i(1, ma), i(2, 2), i(mi, ma) }, // right only upper overflow
				{ i(ma, ma), i(ma, ma), i(-2, -2) }, // right big overflow
				{ i(mi, mi), i(mi, mi), i(0, 0) }, // left big overflow
				{ i(mi+1, ma-1), i(-2, 2), i(mi, ma) }, // both sides overflow
				});
	}

	@Test
	public void test() {
		final Interval result = Interval.plus(a, b);
		assertEquals(expected, result);
	}

	@Test
	public void testCommutative() {
		final Interval resultA = Interval.plus(a, b);
		final Interval resultB = Interval.plus(b, a);
		assertEquals(resultA, resultB);
	}
}
