package ch.ethz.sae;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static ch.ethz.sae.IntervalHelper.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class IntervalModuloTest extends IntervalBinaryOperationTest {

	public IntervalModuloTest(Interval a, Interval b, Interval expected) {
		// TODO maybe be precise?
		super(a, b, expected, false, false);
	}

	@Parameterized.Parameters
	public static Collection<Interval[]> intervals() {
		int m = ma;
		int x = mi;
		for (int i = 6104; i <= 6160; ++i) {
			for (int j = 100; j <= 111; ++j) {
				if (i%j > x)
					x = i%j;
				if (i%j < m)
					m = i%j;
			}
		}
		Random rand = new Random();
		for (int k = 0; k <= 0; ++k) {
			m = ma;
			x = mi;
			int x1 = rand.nextInt();
			int x2 = x1;
			if (x1 != ma && x1 > 0)
				x2 += rand.nextInt(Math.min(ma-x1, 100));
			else if (x1 < 0)
				x2 += rand.nextInt(100);
			int y1 = rand.nextInt();
			int y2 = y1;
			if (y1 != ma && y1 > 0)
				y2 += rand.nextInt(Math.min(ma-y1, 100));
			else if (y1 < 0)
				y2 += rand.nextInt(100);
			for (long i = x1; i <= x2; ++i) {
				for (long j = y1; j <= y2; ++j) {
					if (i%j > x)
						x = (int) (i%j);
					if (i%j < m)
						m = (int) (i%j);
				}
			}
			if (!Interval.modulo(i(x1, x2), i(y1, y2)).contains(i(m, x))) {
				System.out.printf("[%d, %d]%%[%d, %d] = [%d, %d]\n", x1, x2, y1, y2, m, x);
				System.err.println("error");
				System.exit(1);
			}
		}
		
		return Arrays.asList(new Interval[][] {
				{ i(10), i(5), i(0) }, // point
				{ i(10), i(3), i(1) }, // .
				{ i(1), i(0), i(mi, ma) }, // . division by zero
				{ i(ma-1), i(ma), i(ma-1) }, // . boundaries
				{ i(ma), i(ma), i(0) }, // . .
				{ i(ma), i(mi), i(ma) }, // . .
				{ i(ma), i(4), i(3) }, // . .
				{ i(mi), i(ma), i(-1) }, // . .
				{ i(mi), i(mi), i(0) }, // . .
				{ i(mi+1), i(mi), i(mi+1) }, // . .
				{ i(mi), i(4), i(0) }, // . .
				{ i(8, 10), i(3), i(0, 2) }, // range point
				{ i(7, 10), i(3), i(0, 2) }, // .
				{ i(6, 10), i(3), i(0, 2) }, // .
				{ i(100, 120), i(54, 100), i(0, 59) }, // .
				{ i(0, 4), i(2), i(0, 1) }, // .
				{ i(1, 2), i(0), i(mi, ma) }, // . division by zero
				{ i(0, ma), i(2), i(0, 1) }, // . boundaries
				{ i(mi, 0), i(2), i(-1, 0) }, // . .
				{ i(mi, ma), i(2), i(-1, 1) }, // . .
				// FIXME
				
				{ i(6160), i(100, 111), i(0, 100) },
				//{ i(-2, 2), i(3, 3), i(-2, 2) }, // .
				//{ i(10, 10), i(1, 5), i(0, 2) }, // point range
				
				{ i(6159, 6160), i(100, 111), i(0, 109) }, // range range
				{ i(6105, 6160), i(100, 111), i(0, 109) },
				{ i(6104, 6160), i(100, 111), i(0, 110) },
				{ i(900712816, 900712830), i(16050, 16105), i() },
				{ i(1370151185, 1370151229), i(24774, 24799), i() },
				{ i(1397571597, 1397571686), i(2120,2149), i() },
				{ i(1, 2), i(-1, 1), i(mi, ma) }, // . division by zero
				});
	}
	
	@Test
	public void testSymetry() {
		Interval res = operation(a, p);
		if (p.lower != mi)
			assertEquals(operation(a, p), operation(a, i(-p.upper, -p.lower)));
		if (a.lower != mi && res.lower != mi) {
			assertEquals(operation(i(-a.upper, -a.lower), p), i(-res.upper, -res.lower));
			if (p.lower != mi)
				assertEquals(operation(i(-a.upper, -a.lower), i(-p.upper, -p.lower)), i(-res.upper, -res.lower));
		}
	}

	@Override
	protected Interval operation(Interval a, Interval b) {
		return Interval.modulo(a, b);
	}
}
