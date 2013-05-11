package ch.ethz.sae;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public abstract class IntervalBasicOperationTest<Param, Result> {
	
	protected final Interval a;
	protected final Param p;
	protected final Result expected;
	
	public IntervalBasicOperationTest(Interval a, Param p, Result expected) {
		this.a = a;
		this.p = p;
		this.expected = expected;
	}

	@Test
	public void test() {
		final Result result = operation(a, p);
		assertEquals(expected, result);
	}

	protected abstract Result operation(Interval a, Param p);
}
