package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalOrTest extends IntervalBinaryOperationTest {

	public IntervalOrTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, true);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(0), i(0), i(0) }, // point
				{ i(0), i(ma), i(ma) }, // .
				{ i(0), i(mi), i(mi) }, // .
				{ i(3072), i(511), i(3072|511) }, // .
				{ i(3072), i(1024), i(3072) }, // .
				{ i(0), i(mi, ma), i(mi, ma) }, // point range
				{ i(14214), i(mi, ma), i(mi|14214, ma) }, // .
				{ i(-1), i(-5135, 6436), i(-1) }, // .
				{ i(1), i(-5134, 6436), i(-5133, 6437) }, // .
				{ i(0), i(-5135, 6436), i(-5135, 6436) }, // .
				{ i(ma), i(-1, 0), i(-1, ma) }, // .
				{ i(mi+1), i(0, 1), i(mi+1) }, // .
				{ i(mi, ma), i(mi, ma), i(mi, ma) }, // range
				{ i(4, 8), i(0xffa, 0xfc12), i(0xffa, 0xfc1a) }, // .
				// TODO maybe some random generated
				{ i(1), i(1), i(1) }, // pos pos
				{ i(1), i(-1), i(-1) }, // pos neg
				{ i(-1), i(-1), i(-1) }, // neg neg
				{ i(1), i(-1, 1), i(-1, 1) }, // pos mix
				{ i(-1), i(-1, 1), i(-1) }, // neg mix
				{ i(-1, 1), i(-1, 1), i(-1, 1) }, // mix mix
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.or(a, b);
	}
}
