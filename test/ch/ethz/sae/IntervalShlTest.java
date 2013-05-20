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
				{ i(1), i(0), i(1) }, // point
				{ i(1), i(1), i(2) }, // .
				{ i(1), i(31), i(mi) }, // .
				{ i(2), i(31), i(0) }, // .
				{ i(636346), i(32), i(636346) }, // .
				{ i(ma), i(31), i(mi) }, // . boundaries
				{ i(mi), i(31), i(0) }, // . .
				{ i(5325325), i(mi), i(5325325) }, // . .
				{ i(5325326), i(mi), i(5325326) }, // . .
				{ i(-5325325), i(mi), i(-5325325) }, // . .
				{ i(-5325326), i(mi), i(-5325326) }, // . .
				{ i(5325325), i(ma), i(mi) }, // . .
				{ i(5325326), i(ma), i(0) }, // . .
				{ i(-5325325), i(ma), i(mi) }, // . .
				{ i(-5325326), i(ma), i(0) }, // . .
				{ i(0), i(mi), i(0) }, // point range
				{ i(1), i(0, 30), i(1, b(30)) }, // .
				{ i(1), i(2, 25), i(4, b(25)) }, // .
				{ i(1), i(0, 31), i(mi, b(30)) }, // .
				{ i(2), i(0, 31), i(mi, b(30)) }, // .
				{ i(mi), i(0, 31), i(mi, 0) }, // . boundaries
				{ i(ma), i(0, 31), i(mi, ma) }, // . .
				{ i(0, 3), i(30), i(mi, b(30)) }, // range point
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
