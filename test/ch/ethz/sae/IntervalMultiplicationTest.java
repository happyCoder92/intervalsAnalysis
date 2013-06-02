package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static ch.ethz.sae.IntervalHelper.*;

@RunWith(Parameterized.class)
public class IntervalMultiplicationTest extends IntervalBinaryOperationTest {

	public IntervalMultiplicationTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, true);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(0), i(5312512), i(0) }, // point
				{ i(1), i(5312512), i(5312512) }, // .
				{ i(2), i(1000), i(2000) }, // .
				{ i(b(16)), i(b(15)), i(mi) }, // . smallest sign overflow
				{ i(b(16)), i(b(16)), i(0) }, // . smallest overflow
				{ i(ma), i(1), i(ma) }, // . boundaries
				{ i(ma), i(2), i(-2) }, // . . overflow
				{ i(ma), i(ma), i(1) }, // . . .
				{ i(mi), i(1), i(mi) }, // . .
				{ i(mi), i(2), i(0) }, // . . overflow
				{ i(mi), i(3), i(mi) }, // . . .
				{ i(mi), i(mi), i(0) }, // . . .
				
				{ i(0, ma), i(mi, -1), i(mi, ma)}, // l*u overflow
				{ i(0, ma), i(0, ma), i(mi, ma)}, // u*u overflow
				{ i(0, 49), i(0, ma), i(mi, ma)}, // overflow
				
				// No overflow cases
				{ i(2,5), i(4,6), i(8,30) },
				{ i(2,5), i(-6,-4), i(-30,-8) },
				{ i(2,5), i(-4,6), i(-20,30) },
				{ i(-5,-2), i(4,6), i(-30,-8) },
				{ i(-5,-2), i(-6,-4), i(8,30) },
				{ i(-5,-2), i(-4,6), i(-30,20) },
				{ i(-2,5), i(4,6), i(-12,30) },
				{ i(-2,5), i(-6,-4), i(-30,12) },
				{ i(-7,5), i(-4,6), i(-42,30) },
				{ i(-5,1), i(-5,2), i(-10,25) },
				{ i(-4,6), i(-7,5), i(-42,30) },
				{ i(-5,2), i(-5,1), i(-10,25) },
				//Considering also 0 for i1
				{ i(0,5), i(4,6), i(0,30) },
				{ i(0,5), i(-6,-4), i(-30,0) },
				{ i(0,5), i(-4,6), i(-20,30) },
				{ i(-5,0), i(4,6), i(-30,0) },
				{ i(-5,0), i(-6,-4), i(0,30) },
				{ i(-5,0), i(-4,6), i(-30,20) },
				//Considering also 0 for i2
				{ i(2,5), i(0,6), i(0,30) },
				{ i(2,5), i(-6,0), i(-30,0) },
				{ i(-5,-2), i(0,6), i(-30,0) },
				{ i(-5,-2), i(-6,0), i(0,30) },
				{ i(-2,5), i(0,6), i(-12,30) },
				{ i(-2,5), i(-6,0), i(-30,12) },
				//Considering also 0 for both
				{ i(0,5), i(0,6), i(0,30) },
				{ i(0,5), i(-6,0), i(-30,0) },
				{ i(-5,0), i(0,6), i(-30,0) },
				{ i(-5,0), i(-6,0), i(0,30) },
		});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.multiply(a, b);
	}

}
