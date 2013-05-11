package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static ch.ethz.sae.IntervalHelper.*;

@RunWith(Parameterized.class)
public class IntervalDivideTest extends IntervalBinaryOperationTest {

	public IntervalDivideTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, false);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(1, 1), i(1, 1), i(1, 1) }, // point
				{ i(1, 1), i(2, 2), i(0, 0) }, // .
				{ i(mi, mi), i(mi, mi), i(1, 1) }, // . boundaries
				{ i(ma, ma), i(mi, mi), i(0, 0) }, // . .
				{ i(mi, mi), i(ma, ma), i(-1, -1) }, // . .
				{ i(ma, ma), i(ma, ma), i(1, 1) }, // . .
				{ i(mi+1, mi+1), i(-1, -1), i(ma, ma) }, // . .
				{ i(ma, ma), i(-1, -1), i(mi+1, mi+1) }, // . .
				{ i(mi, mi), i(-1, -1), i(mi, mi) }, // . . overflow
				{ i(0, 0), i(0, 0), i(mi, ma) }, // . division by zero should go to top
				// FIXME
				/*{ i(1, 1), i(0, 0), i(1, 1) }, // point range
				{ i(1, 1), i(0, 0), i(1, 1) }, // range point
				{ i(1, 1), i(0, 0), i(1, 1) }, // range range*/
				
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.divide(a, b);
	}
}
