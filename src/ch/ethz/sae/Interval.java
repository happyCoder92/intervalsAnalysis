package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class Interval {

	private static class BinaryInterval {
		private final int base;
		private final int n;

		private BinaryInterval(int base, int n) {
			assert n <= 32 && n >= 0 : n;
			this.base = base & (~(b(32-n)-1));
			this.n = n;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			int b = 0x80000000;
			for (int i = 0; i < n; ++i) {
				sb.append((base & b) != 0 ? '1' : '0');
				b >>>= 1;
			}
			for (int i = 0; i < 32 - n; ++i) {
				sb.append('x');
			}
			return sb.toString();
		}
	}

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

	public static Interval complement(Interval a, Interval b) {
		if (b.contains(a)) {
			return i();
		}
		if (b.isEmpty() || (a.lower < b.lower && a.upper > b.upper)) {
			return a;
		}
		if (a.upper < b.lower || a.lower > b.upper) {
			return a;
		}
		if (b.upper < a.upper) {
			return i(b.upper+1, a.upper);
		}
		return i(a.lower, b.lower-1);
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

	public List<BinaryInterval> splitIntoBinary() {
		List<BinaryInterval> result = new ArrayList<BinaryInterval>();
		int begin = lower;
		while (begin <= upper) {
			int base = 1 << 31;
			for (int i = 1; i <= 32; ++i) {
				int x = ~(base - 1) & begin;
				if (contains(x) && contains(x | (base - 1))) {
					result.add(new BinaryInterval(x, i));
					if ((x | (base - 1)) == ma)
						return result;
					begin = (x | (base - 1)) + 1;
					break;
				}
				base >>>= 1;
			}
		}
		return result;
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
				|| getOverflowType((long)i1.upper * i2.upper) != 0) {
			if (i1.size()*i2.size() < 1000) {
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
		List<BinaryInterval> bi1 = i1.splitIntoBinary();
		List<BinaryInterval> bi2 = i2.splitIntoBinary();
		int min = ma;
		int max = mi;
		ListIterator<BinaryInterval> it1 = bi1.listIterator();
		while (it1.hasNext()) {
			BinaryInterval a = it1.next();
			int maska = ((1 << (32 - a.n)) - 1);
			ListIterator<BinaryInterval> it2 = bi2.listIterator();
			while (it2.hasNext()) {
				BinaryInterval b = it2.next();
				int maskb = ((1 << (32 - b.n)) - 1);
				int x1 = a.base | b.base;
				int x2 = a.base | b.base;
				if (a.n > b.n) {
					x2 |= maskb;
				} else {
					x2 |= maska;
				}
				min = min(min, min(x1, x2));
				max = max(max, max(x1, x2));
			}
		}
		return i(min, max);
	}

	public static Interval and(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		List<BinaryInterval> bi1 = i1.splitIntoBinary();
		List<BinaryInterval> bi2 = i2.splitIntoBinary();
		int min = ma;
		int max = mi;
		ListIterator<BinaryInterval> it1 = bi1.listIterator();
		while (it1.hasNext()) {
			BinaryInterval a = it1.next();
			int maska = ((1 << (32 - a.n)) - 1);
			ListIterator<BinaryInterval> it2 = bi2.listIterator();
			while (it2.hasNext()) {
				BinaryInterval b = it2.next();
				int maskb = ((1 << (32 - b.n)) - 1);
				int x1 = (a.base | maska) & (b.base | maskb);
				int x2 = a.base & b.base;
				if (a.n > b.n) {
					x2 &= ~maskb;
				} else {
					x2 &= ~maska;
				}
				min = min(min, min(x1, x2));
				max = max(max, max(x1, x2));
			}
		}
		return i(min, max);
	}

	public static Interval xor(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		List<BinaryInterval> bi1 = i1.splitIntoBinary();
		List<BinaryInterval> bi2 = i2.splitIntoBinary();
		int min = ma;
		int max = mi;
		ListIterator<BinaryInterval> it1 = bi1.listIterator();
		while (it1.hasNext()) {
			BinaryInterval a = it1.next();
			ListIterator<BinaryInterval> it2 = bi2.listIterator();
			while (it2.hasNext()) {
				BinaryInterval b = it2.next();
				int mn = min(b.n, a.n);
				int mask = ((1 << (32 - mn)) - 1);
				int x = (b.base & ~mask) ^ (a.base & ~mask);
				min = min(min, min(x, x | mask));
				max = max(max, max(x, x | mask));
			}
		}
		return i(min, max);
	}
	
	private static List<Interval> shiftIntervals(Interval i) {
		if (i.size() >= 32) {
			return Arrays.asList(i(0, 31));
		}
		int x = i.lower % 32;
		int y = i.upper % 32;
		if (x < 0)
			x += 32;
		if (y < 0)
			y += 32;
		if (x <= y) {
			return Arrays.asList(i(x, y));
		}
		return Arrays.asList(i(0, y), i(x, 31));
	}
	
	private static Interval shiftInterval(Interval i) {
		Interval result = i();
		for (Interval si : shiftIntervals(i)) {
			result = union(result, si);
		}
		return result;
	}

	public static Interval shl(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		List<Interval> si = shiftIntervals(i2);
		List<BinaryInterval> bi1 = i1.splitIntoBinary();
		int min = ma;
		int max = mi;
		ListIterator<BinaryInterval> it1 = bi1.listIterator();
		while (it1.hasNext()) {
			BinaryInterval a = it1.next();
			int mask = ((1 << (32 - a.n)) - 1);
			int ones = a.base|mask;
			for (Interval sint : si) {
				for (int i = sint.lower; i <= sint.upper; ++i) {
					int x = a.base<<i;
					int y = ones<<i;
					min = min(min, min(x, y));
					max = max(max, max(x, y));
					if (a.n <= i) {
						min = min(min, min(x^0x80000000, y^0x80000000));
						max = max(max, max(x^0x80000000, y^0x80000000));
					}
				}
			}
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
