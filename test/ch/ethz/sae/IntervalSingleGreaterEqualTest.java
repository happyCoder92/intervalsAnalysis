package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalSingleGreaterEqualTest extends IntervalBinaryOperationTest {

	public IntervalSingleGreaterEqualTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, false);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(0), i(0), i(0) }, // point
				{ i(0), i(1), i() }, // .
				{ i(1), i(0), i(1) }, // .
				{ i(ma), i(mi), i(ma) }, // . boundary
				{ i(mi), i(ma), i() }, // . .
				{ i(ma), i(ma), i(ma) }, // . .
				{ i(mi), i(mi), i(mi) }, // . .
				{ i(0, 10), i(10), i(10) }, // range point
				{ i(0, ma), i(ma), i(ma) }, // . boundary
				{ i(mi, 0), i(ma), i() }, // . .
				{ i(0, ma), i(mi), i(0, ma) }, // . .
				{ i(mi, 0), i(mi), i(mi, 0) }, // . .
				{ i(mi, ma), i(mi), i(mi, 0) }, // . .
				{ i(mi, ma), i(ma), i(ma) },
				{ i(0, 10), i(10, 20), i(10) }, // ranges
				{ i(10, 20), i(0, 10), i(10, 20) }, // .
				{ i(mi, 0), i(mi, 0), i(mi, 0) }, // . boundary
				{ i(mi, 0), i(0, ma), i(0) }, // . .
				{ i(mi, 0), i(mi, ma), i(mi, 0) }, // . .
				{ i(0, ma), i(mi, 0), i(0, ma) }, // . .
				{ i(0, ma), i(0, ma), i(0, ma) }, // . .
				{ i(0, ma), i(mi, ma), i(0, ma) }, // . .
				{ i(mi, ma), i(mi, 0), i(mi, ma) }, // . .
				{ i(mi, ma), i(0, ma), i(0, ma) }, // . .
				{ i(mi, ma), i(mi, ma), i(mi, ma) }, // . .
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.singleGreaterEqual(a, b);
	}
}
