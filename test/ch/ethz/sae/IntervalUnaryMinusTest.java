package ch.ethz.sae;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalUnaryMinusTest {

	private final Interval a;
	private final Interval expected;

	public IntervalUnaryMinusTest(Interval a, Interval expected) {
		this.a = a;
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
				{ i(10, 10), i(-10, -10) },
				{ i(-10, 20), i(-20, 10) },
				{ i(mi, mi), i(mi, mi) },
				{ i(ma, ma), i(mi+1, mi+1) },
				{ i(mi, mi+1), i(mi, ma) },
				});
	}

	@Test
	public void test() {
		final Interval result = Interval.minus(a);
		assertEquals(expected, result);
	}

}
