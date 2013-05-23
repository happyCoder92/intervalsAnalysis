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
		// FIXME
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
		});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.multiply(a, b);
	}

}
