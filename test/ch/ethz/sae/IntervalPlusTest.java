package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static ch.ethz.sae.IntervalHelper.*;

@RunWith(Parameterized.class)
public class IntervalPlusTest extends IntervalBinaryOperationTest {

	public IntervalPlusTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected);
	}
	
	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(10), i(20), i(30) }, // point case
				{ i(0, 10), i(0, 10), i(0, 20) }, // ranges cases
				{ i(0, 10), i(0, 20), i(0, 30) }, // .
				{ i(0, 20), i(-10, 10), i(-10, 30) }, // .
				{ i(ma), i(1), i(mi, mi) }, // right overflow at point
				{ i(mi), i(-1), i(ma, ma) }, // left overflow at point
				{ i(ma-1, ma), i(1, 10), i(mi, ma) }, // right range overflow
				{ i(ma), i(1, 10), i(mi, mi+9) }, // right point-range overflow
				{ i(mi, mi+1), i(-1, 10), i(mi, ma) }, // left range overflow
				{ i(mi), i(-10, -1), i(ma-9, ma) }, // left point-range overflow
				{ i(mi, 1), i(-2), i(mi, ma) }, // left only range-point lower overflow
				{ i(1, ma), i(2), i(mi, ma) }, // right only range-point upper overflow
				{ i(ma), i(ma), i(-2, -2) }, // right big overflow
				{ i(mi), i(mi), i(0) }, // left big overflow
				{ i(mi+1, ma-1), i(-2, 2), i(mi, ma) }, // both sides overflow
				{ i(mi, ma), i(-2, 2), i(mi, ma) }, // both sides overflow
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.plus(a, b);
	}
}
