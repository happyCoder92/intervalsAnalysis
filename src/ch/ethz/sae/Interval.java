package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.*;
import static ch.ethz.sae.BinaryHelper.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.List;

public class Interval {
	public Interval() {
		lower = 1;
		upper = 0;
	}

	public Interval(int start_value) {
		lower = upper = start_value;
	}

	public Interval(int l, int u) {
		if (u < l)
			throw new IllegalArgumentException("l has to be lower than u");
		lower = l;
		upper = u;
	}

	@Override
	public String toString() {
		return String.format("[%d,%d]", lower, upper);
	}

	public long size() {
		if (isEmpty()) {
			return 0;
		}
		return (long) upper - lower + 1;
	}

	public boolean contains(Interval a) {
		return a.isEmpty()
				|| (!isEmpty() && lower <= a.lower && upper >= a.upper);
	}

	public boolean contains(int i) {
		return !isEmpty() && lower <= i && upper >= i;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Interval))
			return false;
		Interval i = (Interval) o;
		return lower == i.lower && upper == i.upper;
	}

	public boolean isEmpty() {
		return lower > upper;
	}
	
	public boolean isBottom() {
		return isEmpty();
	}
	
	public boolean isTop() {
		return lower == mi && upper == ma;
	}

	final int lower;
	final int upper;

	public static Interval union(Interval a, int i) {
		if (a.isEmpty()) {
			return i(i);
		}
		return i(min(a.lower, i), max(a.upper, i));
	}

	public static Interval union(Interval a, Interval b) {
		if (a.isEmpty()) {
			return b;
		}
		if (b.isEmpty()) {
			return a;
		}
		return i(min(a.lower, b.lower), max(a.upper, b.upper));
	}

	public static Interval intersect(Interval a, Interval b) {
		if (a.isEmpty() || b.isEmpty()) {
			return i();
		}
		if (a.contains(b)) {
			return b;
		}
		if (b.contains(a)) {
			return a;
		}
		if (a.upper < b.lower || a.lower > b.upper) {
			return i();
		}
		if (a.lower <= b.lower) {
			return i(b.lower, a.upper);
		}
		return i(a.lower, b.upper);
	}

	public static Interval top() {
		return i(mi, ma);
	}

	public static Interval bottom() {
		return i();
	}

	private static int getOverflowType(long val) {
		if (val > ma) {
			return 1;
		}
		if (val < mi) {
			return -1;
		}
		return 0;
	}

	// TRANSFORMERS
	public static Interval plus(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (getOverflowType((long) i1.upper + i2.upper) != getOverflowType((long) i1.lower
				+ i2.lower)) {
			return i(mi, ma);
		}
		return i(i1.lower + i2.lower, i1.upper + i2.upper);
	}

	public static Interval minus(Interval i) {
		if (i.isEmpty()) {
			throw new IllegalArgumentException("interval cannot be empty");
		}
		if (i.lower == mi && i.upper != mi) {
			return i(mi, ma);
		}
		return i(-i.upper, -i.lower);
	}

	public static Interval minus(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (getOverflowType((long) i1.upper - i2.lower) != getOverflowType((long) i1.lower
				- i2.upper)) {
			return i(mi, ma);
		}
		return i(i1.lower - i2.upper, i1.upper - i2.lower);
	}

	public static Interval divide(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (i2.contains(0)) {
			return top();
		}
		if (i1.lower == mi && i2.upper == -1) {
			// overflow case
			if (i1.upper >= mi+1) {
				return i(mi, ma);
			}
			if (i2.lower <= -2) {
				return i(mi, mi/-2);
			}
			return i(mi);
		}
		if (i2.lower < 0) {
			if (i1.upper < 0) {
				return i(i1.upper/i2.lower, i1.lower/i2.upper);
			}
			if (i1.lower >= 0) {
				return i(i1.upper/i2.upper, i1.lower/i2.lower);
			}
			return i(i1.upper/i2.upper, i1.lower/i2.upper);
		}
		if (i1.upper <= 0) {
			return i(i1.lower/i2.lower, i1.upper/i2.upper);
		}
		if (i1.lower >= 0) {
			return i(i1.lower/i2.upper, i1.upper/i2.lower);
		}
		return i(i1.lower/i2.lower, i1.upper/i2.lower);
	}

	public static Interval multiply(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (getOverflowType((long)i1.lower * i2.lower) != 0
				|| getOverflowType((long)i1.upper * i2.upper) != 0
				|| getOverflowType((long)i1.lower * i2.upper) != 0
				|| getOverflowType((long)i1.upper * i2.lower) != 0) {
			if (i1.size() <= 50 && i1.size()*i2.size() <= 50) {
				int min = ma;
				int max = mi;
				for (long i = i1.lower; i <= i1.upper; ++i) {
					for (long j = i2.lower; j <= i2.upper; ++j) {
						int result = (int)i*(int)j;
						min = min(min, result);
						max = max(max, result);
					}
				}
				return i(min, max);
			}
			return top();
		}
		if (i1.lower >= 0) {
			if (i2.lower >= 0) {
				return i(i1.lower*i2.lower, i1.upper*i2.upper);
			}
			if (i2.upper <= 0) {
				return i(i1.upper*i2.lower, i1.lower*i2.upper);
			}
		}
		if (i1.upper <= 0) {
			if (i2.lower >= 0) {
				return i(i1.lower*i2.upper, i1.upper*i2.lower);
			}
			if (i2.upper <= 0) {
				return i(i1.upper*i2.upper, i1.lower*i2.lower);
			}
		}
		return i(min(i1.lower*i2.upper,i1.upper*i2.lower), max(i1.lower*i2.lower, i1.upper*i2.upper));
	}

	public static Interval or(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		int l1 = i1.lower;
		int l2 = i2.lower;
		int u1 = i1.upper;
		int u2 = i2.upper;
		if (u1 < 0) {
			if (u2 < 0) {
				// neg neg
				return i(orMin(l1&ma, u1&ma, l2&ma, u2&ma)|mi, orMax(l1&ma, u1&ma, l2&ma, u2&ma)|mi);
			}
			if (l2 < 0) {
				// neg mix
				return i(l1, -1);
			}
			// neg pos
			return i(orMin(l1&ma, u1&ma, l2, u2)|mi, orMax(l1&ma, u1&ma, l2, u2)|mi);	
		}
		if (l1 < 0) {
			if (u2 < 0) {
				// mix neg
				return i(l2, -1);
			}
			if (l2 < 0) {
				// mix mix
				return i(min(l1, l2), orMax(0, u1, 0, u2));	
			}
			// mix pos
			return i(orMin(l1&ma, ma, l2, u2)|mi, orMax(0, u1, l2, u2));
		}
		if (u2 < 0) {
			// pos neg
			return i(orMin(l1, u1, l2&ma, u2&ma)|mi, orMax(l1, u1, l2&ma, u2&ma)|mi);
		}
		if (l2 < 0) {
			// pos mix
			return i(orMin(l1, u1, l2&ma, ma)|mi, orMax(l1, u1, 0, u2));
		}
		// pos pos
		return i(orMin(l1, u1, l2, u2), orMax(l1, u1, l2, u2));
	}

	public static Interval and(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		int l1 = i1.lower;
		int l2 = i2.lower;
		int u1 = i1.upper;
		int u2 = i2.upper;
		if (u1 < 0) {
			if (u2 < 0) {
				// neg neg
				return i(andMin(l1&ma, u1&ma, l2&ma, u2&ma)|mi, andMax(l1&ma, u1&ma, l2&ma, u2&ma)|mi);
			}
			if (l2 < 0) {
				// neg mix
				return i(andMin(l1&ma, u1&ma, l2&ma, ma)|mi, andMax(l1&ma, u1&ma, 0, u2));	
			}
			// neg pos
			return i(andMin(l1&ma, u1&ma, l2, u2), andMax(l1&ma, u1&ma, l2, u2));
		}
		if (l1 < 0) {
			if (u2 < 0) {
				// mix neg
				return i(andMin(l1&ma, ma, l2&ma, u2&ma)|mi, andMax(0, u1, l2&ma, u2&ma));
			}
			if (l2 < 0) {
				// mix mix
				return i(andMin(l1&ma, ma, l2&ma, ma)|mi, max(u1, u2));	
			}
			// mix pos
			return i(0, u2);
		}
		if (u2 < 0) {
			// pos neg
			return i(andMin(l1, u1, l2&ma, u2&ma), andMax(l1, u1, l2&ma, u2&ma));
		}
		if (l2 < 0) {
			// pos mix
			return i(0, u1);
		}
		// pos pos
		return i(andMin(l1, u1, l2, u2), andMax(l1, u1, l2, u2));
	}
	
	

	public static Interval xor(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		int l1 = i1.lower;
		int l2 = i2.lower;
		int u1 = i1.upper;
		int u2 = i2.upper;
		if (u1 < 0) {
			if (u2 < 0) {
				// neg neg
				return i(xorMin(l1&ma, u1&ma, l2&ma, u2&ma), xorMax(l1&ma, u1&ma, l2&ma, u2&ma));
			}
			if (l2 < 0) {
				// neg mix
				return i(xorMin(l1&ma, u1&ma, 0, u2)|mi, xorMax(l1&ma, u1&ma, l2&ma, ma));	
			}
			// neg pos
			return i(xorMin(l1&ma, u1&ma, l2, u2)|mi, xorMax(l1&ma, u1&ma, l2, u2)|mi);
		}
		if (l1 < 0) {
			if (u2 < 0) {
				// mix neg
				return i(xorMin(0, u1, l2&ma, u2&ma)|mi, xorMax(l1&ma, ma, l2&ma, u2&ma));
			}
			if (l2 < 0) {
				// mix mix
				return i(min(xorMin(0, u1, l2&ma, ma)|mi, xorMin(l1&ma, ma, 0, u2)|mi),
						max(xorMax(0, u1, 0, u2), xorMax(l1&ma, ma, l2&ma, ma)));	
			}
			// mix pos
			return i(xorMin(l1&ma, ma, l2, u2)|mi, xorMax(0, u1, l2, u2));
		}
		if (u2 < 0) {
			// pos neg
			return i(xorMin(l1, u1, l2&ma, u2&ma)|mi, xorMax(l1, u1, l2&ma, u2&ma)|mi);
		}
		if (l2 < 0) {
			// pos mix
			return i(xorMin(l1, u1, l2&ma, ma)|mi, xorMax(l1, u1, 0, u2));
		}
		// pos pos
		return i(xorMin(l1, u1, l2, u2), xorMax(l1, u1, l2, u2));
	}

	public static Interval shl(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		int max = mi;
		int min = ma;
		for (Interval si : shiftIntervals(i2)) {
			Interval r = i1;
			if (si.lower == 0) {
				min = min(min, i1.lower);
				max = max(max, i1.upper);
				if (si.upper == 0) {
					continue;
				}
			}
			if (i1.upper < 0) {
				r = shlPositive(i1.lower&ma, i1.upper&ma, max(1, si.lower), si.upper);
			} else if (i1.lower < 0) {
				r = shlPositive(i1.lower&ma, ma, max(1, si.lower), si.upper);
				min = min(min, r.lower);
				max = max(max, r.upper);
				r = shlPositive(0, i1.upper, max(1, si.lower), si.upper);
			} else {
				r = shlPositive(i1.lower, i1.upper, si.lower, si.upper);
			}
			min = min(min, r.lower);
			max = max(max, r.upper);
		}
		return i(min, max);
	}

	public static Interval shr(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		Interval si = shiftInterval(i2);
		if (i1.lower >= 0) {
			return i(i1.lower>>si.upper, i1.upper>>si.lower);
		}
		if (i1.upper <= 0) {
			return i(i1.lower>>si.lower, i1.upper>>si.upper);
		}
		return i(i1.lower>>si.lower, i1.upper>>si.lower);
	}

	public static Interval slr(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		List<Interval> sis = shiftIntervals(i2);
		Interval si = shiftInterval(i2);
		int lgtz = 31;
		for (Interval i : sis) {
			if (i.upper == 0)
				continue;
			lgtz = min(lgtz, max(i.lower, 1));
		}
		
		if (si.upper == 0) {
			return i1;
		}
		if (i1.lower >= 0) {
			return i(i1.lower>>>si.upper, i1.upper>>>si.lower);
		}
		if (i1.upper < 0) {
			if (si.lower == 0) {
				return i(i1.lower, i1.upper>>>lgtz);
			}
			return i(i1.lower>>>si.upper, i1.upper>>>si.lower);
		}
		if (si.lower == 0) {
			return i(i1.lower, -1>>>lgtz);
		}
		return i(0, -1>>>lgtz);
	}

	public static Interval modulo(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (i2.contains(0)) {
			return top();
		}
		LongInterval div = new LongInterval(i2.lower, i2.upper);
		if (i2.lower < 0) {
			div.upper = -(long)i2.lower;
			div.lower = -(long)i2.upper;
		}
		if (i1.lower < 0) {
			if (i1.upper > 0) {
				LongInterval r1 = modulo(new LongInterval(0, -(long)i1.lower), div);
				LongInterval r2 = modulo(new LongInterval(0, i1.upper), div);
				return i((int)-r1.upper, (int)r2.upper);
			}
			LongInterval result = modulo(new LongInterval(-(long)i1.upper, -(long)i1.lower), div);
			return i((int)-result.upper, (int)-result.lower);
		} else {
			LongInterval result = modulo(new LongInterval(i1.lower, i1.upper), div);
			return i((int)result.lower, (int)result.upper);
		}
		
	}
	
	private static class LongInterval {
		long lower;
		long upper;
		
		LongInterval(long lower, long upper) {
			this.lower = lower;
			this.upper = upper;
		}
	}
	
	private static LongInterval modulo(LongInterval i1, LongInterval i2) {
		long tl = i1.lower / i2.upper;
		long tr = i1.upper / i2.upper;
		long bl = i1.lower / i2.lower;
		long br = i1.upper / i2.lower;
		long m = ma;
		long x = 0;
		long y_rb = i1.upper / (tr + 1) + 1;
		if (tl != tr) {
			m = 0;
			x = i2.upper - 1;
		} else if (bl == br && bl == tl) {
			m = i1.lower % i2.upper;
			x = i1.upper % i2.lower;
		} else {
			m = 0;
			x = y_rb - 2;
		}
		return new LongInterval(m, x);
	}
	
	public static Interval singleLower(Interval a, Interval b) {
		if (a.isEmpty() || b.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (b.upper <= a.lower) {
			return i();
		}
		return i(a.lower, min(a.upper, b.upper-1));
	}
	
	public static Interval singleGreater(Interval a, Interval b) {
		if (a.isEmpty() || b.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (b.lower >= a.upper) {
			return i();
		}
		return i(max(a.lower, b.lower+1), a.upper);
	}
	
	public static Interval singleLowerEqual(Interval a, Interval b) {
		if (a.isEmpty() || b.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (b.upper < a.lower) {
			return i();
		}
		return i(a.lower, min(a.upper, b.upper));
	}
	
	public static Interval singleGreaterEqual(Interval a, Interval b) {
		if (a.isEmpty() || b.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (b.lower > a.upper) {
			return i();
		}
		return i(max(a.lower, b.lower), a.upper);
	}
	
	public static Interval singleNotEqual(Interval a, Interval b) {
		if (a.isEmpty() || b.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (b.lower == b.upper) {
			if (b.lower == a.lower) {
				if (a.lower == a.upper) {
					return i();
				}
				return i(a.lower+1, a.upper);
			}
			if (b.lower == a.upper) {
				return i(a.lower, a.upper-1);
			}
		}
		return a;
	}

	public static Interval widen(Interval a, Interval b) {
		if (a.isBottom()) {
			return b;
		}
		if (b.isBottom()) {
			return a;
		}
		int l = a.lower;
		int u = a.upper;
		if (b.lower < a.lower) {
			l = mi;
		}
		if (b.upper > a.upper) {
			u = ma;
		}
		return i(l, u);
	}
}
