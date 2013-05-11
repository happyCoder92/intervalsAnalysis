package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static ch.ethz.sae.IntervalHelper.*;

@RunWith(Parameterized.class)
public class IntervalMinusTest extends IntervalBinaryOperationTest {

	public IntervalMinusTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, false);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(10, 10), i(20, 20), i(-10, -10) }, // point case
				{ i(0, 10), i(0, 10), i(-10, 10) }, // ranges cases
				{ i(0, 10), i(0, 20), i(-20, 10) }, // .
				{ i(0, 20), i(-10, 10), i(-10, 30) }, // .
				{ i(mi, mi), i(1, 1), i(ma, ma) }, // left overflow at point
				{ i(ma, ma), i(-1, -1), i(mi, mi) }, // right overflow at point
				{ i(mi, mi+1), i(1, 10), i(mi, ma) }, // left range overflow
				{ i(ma-1, ma), i(-10, -1), i(mi, ma) }, // right range overflow
				{ i(mi, 1), i(2, 2), i(mi, ma) }, // left only lower overflow
				{ i(1, ma), i(-2, -2), i(mi, ma) }, // right only upper overflow
				{ i(mi, mi), i(ma, ma), i(1, 1) }, // left big overflow
				{ i(ma, ma), i(mi, mi), i(-1, -1) }, // right big overflow
				{ i(mi+1, ma-1), i(-2, 2), i(mi, ma) }, // both sides overflow
				{ i(mi, ma), i(-2, 2), i(mi, ma) }, // both sides overflow
				// special minus case - that's why it cannot be implemented using plus
				{ i(1, 1), i(mi, mi+1), i(mi, mi+1) },
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.minus(a, b);
	}
}
