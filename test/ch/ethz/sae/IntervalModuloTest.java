package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static ch.ethz.sae.IntervalHelper.*;

@RunWith(Parameterized.class)
public class IntervalModuloTest extends IntervalBinaryOperationTest {

	public IntervalModuloTest(Interval a, Interval b, Interval expected) {
		// TODO maybe be precise?
		super(a, b, expected, false, false);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(10, 10), i(5, 5), i(0, 0) }, // point cases
				{ i(10, 10), i(-5, -5), i(0, 0) }, // point cases
				{ i(10, 10), i(3, 3), i(1, 1) }, // .
				{ i(10, 10), i(-3, -3), i(1, 1) }, // .
				{ i(8, 10), i(3, 3), i(0, 2) }, // range-point cases
				{ i(7, 10), i(3, 3), i(0, 2) }, // .
				{ i(6, 10), i(3, 3), i(0, 2) }, // .
				{ i(-2, 2), i(3, 3), i(-2, 2) }, // .
				{ i(10, 10), i(1, 5), i(0, 2) }, // point-range cases
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.modulo(a, b);
	}
}
