package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.i;
import static ch.ethz.sae.IntervalHelper.ma;
import static ch.ethz.sae.IntervalHelper.mi;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalComplementTest extends IntervalBinaryOperationTest {

	public IntervalComplementTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, false);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> intervals() {
		return Arrays.asList(new Object[][] {
				{ i(10, 10), i(10), i() },
				{ i(9, 11), i(10), i(9, 11) },
				{ i(9, 11), i(12), i(9, 11) },
				{ i(1,5), i(4,5) , i(1,3)},
				{ i(1, 5), i(1,3), i(4, 5) },
				{ i(mi, ma), i(10), i(mi, ma) },
				{ i(mi, ma-1), i(ma), i(mi, ma-1) },
				{ i(mi+1, ma), i(mi), i(mi+1, ma) },
				{ i(), i(10), i() },
				{ i(mi+1, ma), i(mi+1, ma), i() },
				{ i(mi+1, ma), i(mi, ma), i() },
				{ i(mi, ma-1), i(mi, ma-1), i() },
				{ i(mi, ma-1), i(mi, ma), i() },
				{ i(mi, -2), i(2, ma), i(mi, -2) },
				{i(mi, ma), i(mi, 4), i(5, ma)},
				{ i(-3, -2), i(2, 3), i(-3, -2) },
				{ i(-1, 1), i(), i(-1, 1) },
				{ i(), i(10, 20), i() },
				{ i(), i(), i() },
				});
	}
	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.complement(a, b);
	}

	
}
