package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalAndTest extends IntervalBinaryOperationTest {

	public IntervalAndTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, true);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(0), i(0), i(0) }, // point
				{ i(0), i(ma), i(0) }, // .
				{ i(0), i(mi), i(0) }, // .
				{ i(3072), i(511), i(0) }, // .
				{ i(3072), i(1024), i(1024) }, // .
				{ i(0), i(mi, ma), i(0) }, // point range
				{ i(14214), i(mi, ma), i(0, 14214) }, // .
				{ i(-1), i(mi, ma), i(mi, ma) }, // .
				{ i(-1), i(-5135, 6436), i(-5135, 6436) }, // .
				{ i(-2), i(-5135, 6437), i(-5136, 6436) }, // .
				{ i(ma), i(-1, 0), i(0, ma) }, // .
				{ i(mi+1), i(0, 1), i(0, 1) }, // .
				{ i(mi, ma), i(mi, ma), i(mi, ma) }, // range
				{ i(mi, ma), i(15151, 155121), i(0, 155121) }, // .
				// TODO maybe some random generated
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.and(a, b);
	}
}
