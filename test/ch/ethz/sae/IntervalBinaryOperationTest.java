package ch.ethz.sae;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;
import org.junit.experimental.categories.Categories.ExcludeCategory;

public abstract class IntervalBinaryOperationTest extends IntervalBasicOperationTest<Interval, Interval> {
	
	protected final boolean precise;
	protected final boolean commutative;
	
	public IntervalBinaryOperationTest(Interval a, Interval b, Interval expected, boolean precise, boolean commutative) {
		super(a, b, expected);
		this.precise = precise;
		this.commutative = commutative;
	}

	public IntervalBinaryOperationTest(Interval a, Interval b, Interval expected) {
		this(a, b, expected, true, false);
	}

	@Override
	@Test
	public void test() {
		final Interval result = operation(a, p);
		if (precise)
			assertEquals(expected, result);
		else
			assertTrue(result.contains(expected));
	}

	@Test
	public void testCommutative() {
		assumeTrue(commutative);
		final Interval resultA = operation(a, p);
		final Interval resultB = operation(p, a);
		assertEquals(resultA, resultB);
	}
}
