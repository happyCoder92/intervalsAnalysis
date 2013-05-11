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

		});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.multiply(a, b);
	}

}
