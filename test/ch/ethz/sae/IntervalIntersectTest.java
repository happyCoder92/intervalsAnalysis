package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.i;
import static ch.ethz.sae.IntervalHelper.ma;
import static ch.ethz.sae.IntervalHelper.mi;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalIntersectTest extends IntervalBinaryOperationTest {

	public IntervalIntersectTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, true);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> intervals() {
		return Arrays.asList(new Object[][] {
				{ i(10, 10), i(10), i(10, 10) },
				{ i(9, 11), i(10), i(10) },
				{ i(9, 11), i(12), i() },
				{ i(4, 7), i(2, 4), i(4) },
				{ i(2, 7), i(4,8), i(4,7) },
				{ i(mi, ma), i(mi, ma), i(mi, ma) },
				{ i(mi, ma), i(10), i(10) },
				{ i(mi, ma-1), i(ma), i() },
				{ i(mi+1, ma), i(mi), i() },
				{ i(), i(10), i() },
				{ i(mi+1, ma), i(mi+1, ma), i(mi+1, ma) },
				{ i(mi+1, ma), i(mi, ma), i(mi+1, ma) },
				{ i(mi, ma-1), i(mi, ma-1), i(mi, ma-1) },
				{ i(mi, ma-1), i(mi, ma), i(mi, ma-1) },
				{ i(mi, -2), i(2, ma), i() },
				{ i(-3, -2), i(2, 3), i() },
				{ i(-1, 1), i(), i() },
				{ i(1,5), i(2, 4), i(2,4) },
				{ i(), i(), i() },
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.intersect(a, b);
	}
}
