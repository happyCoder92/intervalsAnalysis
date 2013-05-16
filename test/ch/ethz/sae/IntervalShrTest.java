package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.i;
import static ch.ethz.sae.IntervalHelper.ma;
import static ch.ethz.sae.IntervalHelper.mi;

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
				{ i(0), i(0), i(0) }, // point
				{ i(32), i(0), i(32) }, // .
				{ i(32), i(-1), i(0) }, // .
				{ i(32), i(-31), i(16) }, // .
				{ i(32), i(3), i(4) }, // .
				{ i(31), i(3), i(3) }, // .
				{ i(-31), i(3), i(-4) }, // .
				{ i(ma), i(0), i(ma) }, // . boundaries
				{ i(mi), i(0), i(mi) }, // . .
				{ i(ma), i(1), i(ma>>1) }, // . .
				{ i(mi), i(1), i(mi>>1) }, // . .
				{ i(ma), i(mi), i(ma) }, // . .
				{ i(ma), i(ma), i(0) }, // . .
				{ i(mi), i(mi), i(mi) }, // . .
				{ i(mi), i(ma), i(-1) }, // . .
				{ i(0), i(mi), i(0) }, // . .
				{ i(0), i(ma), i(0) }, // . .
				{ i(626252), i(ma), i(0) }, // . .
				{ i(-51251), i(ma), i(-1) }, // . .
				{ i(-51251), i(mi), i(-51251) }, // . .
				{ i(626252), i(mi), i(626252) }, // . .
				{ i(32), i(0, 1), i(16, 32) }, // point range
				{ i(32), i(-1, 1), i(0, 32) }, // .
				{ i(32), i(0, 6), i(0, 32) }, // .
				{ i(32), i(1, 2), i(8, 16) }, // .
				{ i(32), i(2, 6), i(0, 8) }, // .
				{ i(-32), i(0, 1), i(-32, -16) }, // .
				{ i(-32), i(0, 6), i(-32, -1) }, // .
				{ i(-32), i(1, 2), i(-16, -8) }, // .
				{ i(-32), i(2, 6), i(-8, -1) }, // .
				{ i(-32), i(-1, 1), i(-32, -1) }, // .
				{ i(mi), i(0, 1), i(mi, mi>>1) }, // . boundaries
				{ i(mi), i(0, 6), i(mi, mi>>6) }, // . .
				{ i(mi), i(1, 2), i(mi>>1, mi>>2) }, // . .
				{ i(mi), i(2, 6), i(mi>>2, mi>>6) }, // . .
				{ i(mi), i(-1, 1), i(mi, -1) }, // .
				{ i(ma), i(0, 1), i(ma>>1, ma) }, // . .
				{ i(ma), i(0, 6), i(ma>>6, ma) }, // . .
				{ i(ma), i(1, 2), i(ma>>2, ma>>1) }, // . .
				{ i(ma), i(2, 6), i(ma>>6, ma>>2) }, // . .
				{ i(ma), i(-1, 1), i(0, ma) }, // .
				{ i(0, 15), i(0), i(0, 15) }, // range point
				{ i(0, 15), i(2), i(0, 3) }, // .
				{ i(0, 15), i(5), i(0) }, // .
				{ i(0, 15), i(7), i(0) }, // .
				{ i(7, 15), i(0), i(7, 15) }, // .
				{ i(7, 15), i(2), i(1, 3) }, // .
				{ i(7, 15), i(5), i(0) }, // .
				{ i(7, 15), i(7), i(0) }, // .
				{ i(-15, 0), i(0), i(-15, 0) }, // .
				{ i(-15, 0), i(2), i(-4, 0) }, // .
				{ i(-15, 0), i(5), i(-1, 0) }, // .
				{ i(-15, 0), i(7), i(-1, 0) }, // .
				{ i(-15, -7), i(0), i(-15, -7) }, // .
				{ i(-15, -7), i(2), i(-4, -2) }, // .
				{ i(-15, -7), i(5), i(-1) }, // .
				{ i(-15, -7), i(7), i(-1) }, // .
				{ i(-15, 15), i(0), i(-15, 15) }, // .
				{ i(-15, 15), i(2), i(-4, 3) }, // .
				{ i(-15, 15), i(5), i(-1, 0) }, // .
				{ i(-15, 15), i(7), i(-1, 0) }, // .
				{ i(mi, ma), i(ma), i(-1, 0) }, // . boundaries
				{ i(mi, ma), i(mi), i(mi, ma) }, // . .
				{ i(mi, ma), i(0), i(mi, ma) }, // . .
				{ i(mi, ma), i(1), i(mi>>1, ma>>1) }, // . .
				{ i(mi, 0), i(ma), i(-1, 0) }, // . .
				{ i(mi, 0), i(mi), i(mi, 0) }, // . .
				{ i(mi, 0), i(0), i(mi, 0) }, // . .
				{ i(mi, 0), i(1), i(mi>>1, 0) }, // . .
				{ i(mi, -7), i(ma), i(-1) }, // . .
				{ i(mi, -7), i(mi), i(mi, -7) }, // . .
				{ i(mi, -7), i(0), i(mi, -7) }, // . .
				{ i(mi, -7), i(1), i(mi>>1, -4) }, // . .
				{ i(mi, -7), i(4), i(mi>>4, -1) }, // . .
				{ i(0, ma), i(ma), i(0) }, // . .
				{ i(0, ma), i(mi), i(0, ma) }, // . .
				{ i(0, ma), i(0), i(0, ma) }, // . .
				{ i(0, ma), i(1), i(0, ma>>1) }, // . .
				{ i(7, ma), i(ma), i(0) }, // . .
				{ i(7, ma), i(mi), i(7, ma) }, // . .
				{ i(7, ma), i(0), i(7, ma) }, // . .
				{ i(7, ma), i(1), i(3, ma>>1) }, // . .
				{ i(7, ma), i(4), i(0, ma>>4) }, // . .
				{ i(0, 15), i(0, 2), i(0, 15) }, // ranges
				{ i(0, 15), i(2, 4), i(0, 3) }, // .
				{ i(0, 15), i(5, 31), i(0) }, // .
				{ i(7, 15), i(0, 2), i(1, 15) }, // .
				{ i(7, 15), i(2, 4), i(0, 3) }, // .
				{ i(7, 15), i(5, 31), i(0) }, // .
				{ i(-15, 0), i(0, 2), i(-15, 0) }, // .
				{ i(-15, 0), i(2, 4), i(-4, 0) }, // .
				{ i(-15, 0), i(5, 31), i(-1, 0) }, // .
				{ i(-15, -7), i(0, 2), i(-15, -2) }, // .
				{ i(-15, -7), i(2, 4), i(-4, -1) }, // .
				{ i(-15, -7), i(5, 31), i(-1) }, // .
				{ i(mi, 0), i(0, 2), i(mi, 0) }, // . boundaries
				{ i(mi, 0), i(2, 4), i(mi>>2, 0) }, // . .
				{ i(mi, 0), i(5, 31), i(mi>>5, 0) }, // . .
				{ i(mi, -7), i(0, 2), i(mi, -2) }, // . .
				{ i(mi, -7), i(2, 4), i(mi>>2, -1) }, // . .
				{ i(mi, -7), i(5, 31), i(mi>>5, -1) }, // . .
				{ i(0, ma), i(0, 2), i(0, ma) }, // . .
				{ i(0, ma), i(2, 4), i(0, ma>>2) }, // . .
				{ i(0, ma), i(5, 31), i(0, ma>>5) }, // . .
				{ i(7, ma), i(0, 2), i(1, ma) }, // . .
				{ i(7, ma), i(2, 4), i(0, ma>>2) }, // . .
				{ i(7, ma), i(5, 31), i(0, ma>>5) }, // . .
				{ i(mi, ma), i(0, 2), i(mi, ma) }, // . .
				{ i(mi, ma), i(2, 4), i(mi>>2, ma>>2) }, // . .
				{ i(mi, ma), i(5, 31), i(mi>>5, ma>>5) }, // . .
				{ i(mi, ma), i(mi, ma), i(mi, ma) }, // . .
				});
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.shr(a, b);
	}
}
