package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static ch.ethz.sae.IntervalHelper.*;

@RunWith(Parameterized.class)
public class IntervalShlTest extends IntervalBinaryOperationTest {

	public IntervalShlTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, false);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(1, 1), i(0, 0), i(1, 1) }, // point
				{ i(1, 1), i(1, 1), i(2, 2) }, // .
				{ i(1, 1), i(31, 31), i(mi, mi) }, // .
				{ i(1, 1), i(32, 32), i(1, 1) }, // .
				{ i(0, 0), i(mi, ma), i(0, 0) }, // point range
				{ i(1, 1), i(0, 30), i(0, b(30)) }, // .
				{ i(1, 1), i(0, 31), i(mi, b(30)) }, // .
				// FIXME
				// { i(0, 0), i(mi, ma), i(0, 0) }, // range point
				// { i(0, 0), i(mi, ma), i(0, 0) }, // range range
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.shl(a, b);
	}
}
