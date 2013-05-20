package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.i;
import static ch.ethz.sae.IntervalHelper.ma;
import static ch.ethz.sae.IntervalHelper.mi;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalUnaryMinusTest extends IntervalBasicOperationTest<Void, Interval> {
	
	public IntervalUnaryMinusTest(Interval a, Interval expected) {
		super(a, null, expected);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(10, 10), i(-10, -10) },
				{ i(-10, 20), i(-20, 10) },
				{ i(mi), i(mi) },
				{ i(ma), i(mi+1) },
				{ i(mi, mi+1), i(mi, ma) },
				});
	}

	@Override
	protected Interval operation(Interval a, Void p) {
		return Interval.minus(a);
	}
}
