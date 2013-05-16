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
				{ i(1), i(1), i(1) }, // point
				{ i(1), i(2), i(0) }, // .
				{ i(mi), i(mi), i(1) }, // . boundaries
				{ i(ma), i(mi), i(0) }, // . .
				{ i(mi), i(ma), i(-1) }, // . .
				{ i(ma), i(ma), i(1) }, // . .
				{ i(mi+1), i(-1), i(ma) }, // . .
				{ i(ma), i(-1), i(mi+1) }, // . .
				{ i(mi), i(-1), i(mi) }, // . . overflow
				{ i(0), i(0), i(mi, ma) }, // . division by zero should go to top
				{ i(10000), i(1, 10000), i(1, 10000) }, // point range
				{ i(10000), i(1, 1000), i(10, 10000) }, // .
				{ i(10000), i(1, 100000), i(0, 10000) }, // .
				{ i(10000), i(600, 100000), i(0, 16) }, // .
				{ i(10000), i(-10000, -1), i(-10000, -1) }, // .
				{ i(10000), i(-1000, -1), i(-10000, -10) }, // .
				{ i(10000), i(-100000, -1), i(-10000, 0) }, // .
				{ i(10000), i(-100000, -600), i(-16, 0) }, // .
				{ i(-10000), i(1, 10000), i(-10000, -1) }, // .
				{ i(-10000), i(1, 1000), i(-10000, -10) }, // .
				{ i(-10000), i(1, 100000), i(-10000, 0) }, // .
				{ i(-10000), i(600, 100000), i(-16, 0) }, // .
				{ i(-10000), i(-10000, -1), i(1, 10000) }, // .
				{ i(-10000), i(-1000, -1), i(10, 10000) }, // .
				{ i(-10000), i(-100000, -1), i(0, 10000) }, // .
				{ i(-10000), i(-100000, -600), i(0, 16) }, // .
				{ i(ma), i(1, 10000), i(ma/10000, ma) }, // . boundaries
				{ i(ma), i(600, 100000), i(ma/100000, ma/600) }, // . .
				{ i(ma), i(-10000, -1), i(-ma, ma/-10000) }, // . .
				{ i(ma), i(-100000, -600), i(ma/-600, ma/-100000) }, // . .
				{ i(mi), i(1, 10000), i(mi, mi/10000) }, // . .
				{ i(mi), i(600, 100000), i(mi/600, mi/100000) }, // . .
				{ i(mi), i(-10000, -1), i(mi, mi/-2) }, // . . overflow
				{ i(mi), i(-100000, -600), i(mi/-100000, mi/-600) }, // . .
				{ i(0), i(-1, 1), i(mi, ma) }, // . division by zero should go to top
				{ i(1, 52352), i(1), i(1, 52352) }, // range point
				{ i(1, 10), i(2), i(0, 5) }, // .
				{ i(1, 10), i(11), i(0) }, // .
				{ i(1, 52352), i(-1), i(-52352, -1) }, // .
				{ i(1, 10), i(-2), i(-5, 0) }, // .
				{ i(1, 10), i(-11), i(0) }, // .
				{ i(-52352, -1), i(1), i(-52352, -1) }, // .
				{ i(-10, -1), i(2), i(-5, 0) }, // .
				{ i(-10, -1), i(11), i(0) }, // .
				{ i(-52352, -1), i(-1), i(1, 52352) }, // .
				{ i(-10, -1), i(-2), i(0, 5) }, // .
				{ i(-10, -1), i(-11), i(0) }, // .
				{ i(-52352, 52351), i(1), i(-52352, 52351) }, // .
				{ i(-10, 10), i(2), i(-5, 5) }, // .
				{ i(-10, 10), i(11), i(0) }, // .
				{ i(-52352, 52351), i(-1), i(-52351, 52352) }, // .
				{ i(-10, 10), i(-2), i(-5, 5) }, // .
				{ i(-10, 10), i(-11), i(0) }, // .
				{ i(-52352, 52351), i(ma), i(0) }, // . boundaries
				{ i(-52352, 52351), i(mi), i(0) }, // . .
				{ i(0, ma), i(2), i(0, ma/2) }, // . .
				{ i(0, ma), i(-1), i(-ma, 0) }, // . .
				{ i(0, ma), i(mi), i(0) }, // . .
				{ i(0, ma), i(ma), i(0, 1) }, // . .
				{ i(mi, 0), i(2), i(mi/2, 0) }, // . .
				{ i(mi, 0), i(-1), i(mi, ma) }, // . . overflow
				{ i(mi, 0), i(mi), i(0, 1) }, // . .
				{ i(mi, 0), i(ma), i(-1, 0) }, // . .
				{ i(mi, ma), i(2), i(mi/2, ma/2) }, // . .
				{ i(mi, ma), i(-1), i(mi, ma) }, // . . overflow
				{ i(mi, ma), i(mi), i(0, 1) }, // . .
				{ i(mi, ma), i(ma), i(-1, 1) }, // . .
				{ i(-52352, 52351), i(0), i(mi, ma) }, // . division by zero should go to top
				// FIXME
				/*{ i(1, 1), i(0, 0), i(1, 1) }, // range range*/
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.divide(a, b);
	}
}
