package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static ch.ethz.sae.IntervalHelper.*;

@RunWith(Parameterized.class)
public class IntervalUnionElementTest extends IntervalBasicOperationTest<Integer, Interval> {

	public IntervalUnionElementTest(Interval a, Integer i, Interval expected) {
		super(a, i, expected);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> intervals() {
		return Arrays.asList(new Object[][] {
				{ i(10, 10), 10, i(10, 10) },
				{ i(9, 11), 10, i(9, 11) },
				{ i(9, 11), 12, i(9, 12) },
				{ i(9, 11), 8, i(8, 11) },
				{ i(mi, ma), 10, i(mi, ma) },
				{ i(mi, ma-1), ma, i(mi, ma) },
				{ i(mi+1, ma), mi, i(mi, ma) },
				{ i(), 10, i(10, 10) },
				});
	}

	@Override
	protected Interval operation(Interval a, Integer i) {
		return Interval.union(a, i);
	}

}
