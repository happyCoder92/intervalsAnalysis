package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static ch.ethz.sae.IntervalHelper.*;

@RunWith(Parameterized.class)
public class IntervalContainsElementTest extends IntervalBasicOperationTest<Integer, Boolean> {

	public IntervalContainsElementTest(Interval a, Integer i, Boolean expected) {
		super(a, i, expected);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> intervals() {
		return Arrays.asList(new Object[][] {
				{ i(mi, ma), mi, true },
				{ i(mi, ma), ma, true },
				{ i(mi+1, ma), mi, false },
				{ i(mi, ma-1), ma, false },
				{ i(0, 0), 1, false },
				{ i(0, 0), 0, true },
				{ i(), 1, false },
				});
	}

	@Override
	protected Boolean operation(Interval a, Integer i) {
		return a.contains(i);
	}

}
