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
public class IntervalContainsTest extends IntervalBasicOperationTest<Interval, Boolean> {

	public IntervalContainsTest(Interval a, Interval b, Boolean expected) {
		super(a, b, expected);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> intervals() {
		return Arrays.asList(new Object[][] {
				{ i(mi, ma), i(mi), true },
				{ i(mi, ma), i(ma), true },
				{ i(mi+1, ma), i(mi), false },
				{ i(mi, ma-1), i(ma), false },
				{ i(0, 0), i(1), false },
				{ i(0, 0), i(0), true },
				{ i(), i(1), false },
				{ i(mi+1, ma), i(mi, ma), false},
				{ i(mi, ma-1), i(mi, ma), false},
				{ i(mi, ma), i(mi, ma), true},
				{ i(mi, -2), i(2, ma), false},
				{ i(2, ma), i(mi, -2), false},
				{ i(2, 10), i(3, 5), true},
				{ i(2, 10), i(), true },
				{ i(), i(), true },
				{ i(), i(1, 10), false },
				});
	}

	@Override
	protected Boolean operation(Interval a, Interval b) {
		return a.contains(b);
	}
}
