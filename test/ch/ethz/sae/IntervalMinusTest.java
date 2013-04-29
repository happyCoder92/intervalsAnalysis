package ch.ethz.sae;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalMinusTest {
	private final Interval a, b;
	private final Interval expected;

	public IntervalMinusTest(Interval a, Interval b, Interval expected) {
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
				{ i(10, 10), i(20, 20), i(-10, -10) }, // point case
				{ i(0, 10), i(0, 10), i(-10, 10) }, // ranges cases
				{ i(0, 10), i(0, 20), i(-20, 10) }, // .
				{ i(0, 20), i(-10, 10), i(-10, 30) }, // .
				{ i(mi, mi), i(1, 1), i(ma, ma) }, // left overflow at point
				{ i(ma, ma), i(-1, -1), i(mi, mi) }, // right overflow at point
				{ i(mi, mi+1), i(1, 10), i(mi, ma) }, // left range overflow
				{ i(ma-1, ma), i(-10, -1), i(mi, ma) }, // right range overflow
				{ i(mi, 1), i(2, 2), i(mi, ma) }, // left only lower overflow
				{ i(1, ma), i(-2, -2), i(mi, ma) }, // right only upper overflow
				{ i(mi, mi), i(ma, ma), i(1, 1) }, // left big overflow
				{ i(ma, ma), i(mi, mi), i(-1, -1) }, // right big overflow
				{ i(mi+1, ma-1), i(-2, 2), i(mi, ma) }, // both sides overflow
				// special minus case - that's why it cannot be implemented using plus
				{ i(1, 1), i(mi, mi+1), i(mi, mi+1) },
				});
	}

	@Test
	public void test() {
		final Interval result = Interval.minus(a, b);
		assertEquals(expected, result);
	}
}
