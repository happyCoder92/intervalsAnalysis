package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalShrTest extends IntervalBinaryOperationTest {

	public IntervalShrTest(Interval a, Interval b, Interval expected) {
		super(a, b, expected, true, false);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		return Arrays.asList(new Interval[][] {
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.shr(a, b);
	}
}
