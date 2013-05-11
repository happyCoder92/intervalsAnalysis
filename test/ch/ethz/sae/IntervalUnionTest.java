package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.i;
import static ch.ethz.sae.IntervalHelper.ma;
import static ch.ethz.sae.IntervalHelper.mi;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

// FIXME
@RunWith(Parameterized.class)
public class IntervalUnionTest extends IntervalBinaryOperationTest {

	public IntervalUnionTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, true);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> intervals() {
		return Arrays.asList(new Object[][] {
				{ i(10, 10), i(10), i(10, 10) },
				{ i(9, 11), i(10), i(9, 11) },
				{ i(9, 11), i(12), i(9, 12) },
				{ i(9, 11), i(8), i(8, 11) },
				{ i(mi, ma), i(10), i(mi, ma) },
				{ i(mi, ma-1), i(ma), i(mi, ma) },
				{ i(mi+1, ma), i(mi), i(mi, ma) },
				{ i(), i(10), i(10, 10) },
				{ i(mi+1, ma), i(mi+1, ma), i(mi+1, ma) },
				{ i(mi+1, ma), i(mi, ma), i(mi, ma) },
				{ i(mi, ma-1), i(mi, ma-1), i(mi, ma-1) },
				{ i(mi, ma-1), i(mi, ma), i(mi, ma) },
				{ i(mi, -2), i(2, ma), i(mi, ma) },
				{ i(-3, -2), i(2, 3), i(-3, 3) },
				{ i(-1, 1), i(), i(-1, 1) },
				{ i(), i(10, 20), i(10, 20) },
				{ i(), i(), i() },
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.union(a, b);
	}
}
