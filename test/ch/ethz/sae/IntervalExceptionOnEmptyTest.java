package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.i;
import static ch.ethz.sae.IntervalHelper.ma;
import static ch.ethz.sae.IntervalHelper.mi;

import java.rmi.server.Operation;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntervalExceptionOnEmptyTest {

	private interface Operation {
		public Interval operation(Interval a, Interval b);
	}
	
	private Operation op;
	
	public IntervalExceptionOnEmptyTest(Operation op) {
		this.op = op;
	}

	@Parameterized.Parameters
	public static Collection<Operation[]> intervals() {
		return Arrays.asList(new Operation[][] {
				{ 
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.plus(a, b);
						}
					}
				}, {
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.minus(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.divide(a, b);
						}
					}
				}, 
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.multiply(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.modulo(a, b);
						}
					}
				}, 
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.and(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.or(a, b);
						}
					}
				}, 
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.xor(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.shr(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.shl(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.slr(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.singleGreater(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.singleGreaterEqual(a, b);
						}
					}
				}, 
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.singleLower(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.singleLowerEqual(a, b);
						}
					}
				},
				{
					new Operation() {
						public Interval operation(Interval a, Interval b) {
							return Interval.singleNotEqual(a, b);
						}
					}
				}
				});
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAEmpty() {
		op.operation(i(1), i());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBEmpty() {
		op.operation(i(), i(1));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBothEmpty() {
		op.operation(i(), i());
	}
}
