package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static ch.ethz.sae.IntervalHelper.*;

@RunWith(Parameterized.class)
public class IntervalXorTest extends IntervalBinaryOperationTest {

	public IntervalXorTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, true);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(0), i(0), i(0) }, // point
				{ i(0), i(ma), i(ma) }, // .
				{ i(0), i(mi), i(mi) }, // .
				{ i(1), i(mi), i(mi+1) }, // .
				{ i(1), i(ma), i(ma-1) }, // .
				{ i(1), i(0, 1), i(0, 1) }, // point range
				{ i(mi), i(0, 1), i(mi, mi+1) }, // .
				{ i(ma), i(0, 1), i(ma-1, ma) }, // .
				{ i(ma), i(-1, 1), i(mi, ma) }, // .
				{ i(mi), i(-1, 1), i(mi, ma) }, // .
				{ i(ma), i(213, 124142), i(ma^124142, ma^213) }, // .
				{ i(mi), i(412, 16434661), i(mi^412, mi^16434661) }, // .
				{ i(1241), i(412, 16461), i(0, 17663) }, // .
				{ i(534), i(-12322, 13241), i(-12856, 13311) }, // .
				{ i(1, 2), i(1, 2), i(0, 3) }, // range
				{ i(mi, ma), i(mi, ma), i(mi, ma) }, // .
				{ i(mi, ma), i(153151, 2124412), i(mi, ma) }, // .
				// TODO maybe some random generated
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.xor(a, b);
	}
}
