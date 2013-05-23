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
		super(a, b, expected, true, false);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				{ i(10), i(5), i(0) }, // point
				{ i(10), i(-5), i(0) }, // .
				{ i(10), i(3), i(1) }, // .
				{ i(10), i(-3), i(1) }, // .
				{ i(-10), i(5), i(0) }, // .
				{ i(-10), i(-5), i(0) }, // .
				{ i(-10), i(3), i(-1) }, // .
				{ i(-10), i(-3), i(-1) }, // .
				{ i(8, 10), i(3), i(0, 2) }, // range point
				{ i(7, 10), i(3), i(0, 2) }, // .
				{ i(6, 10), i(3), i(0, 2) }, // .
				{ i(100, 120), i(54, 100), i() },
				//{ i(-2, 2), i(3, 3), i(-2, 2) }, // .
				//{ i(10, 10), i(1, 5), i(0, 2) }, // point range
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.modulo(a, b);
	}
}
