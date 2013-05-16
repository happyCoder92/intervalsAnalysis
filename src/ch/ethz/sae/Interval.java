package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.i;
import static ch.ethz.sae.IntervalHelper.ma;
import static ch.ethz.sae.IntervalHelper.mi;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class Interval {

	private static class BinaryInterval {
		private int base;
		private int n;

		private BinaryInterval(int base, int n) {
			this.base = base;
			this.n = n;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			int b = 1 << 31;
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

	// TODO: Do you need to handle infinity or empty interval?
	private final int lower, upper;

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
		if (a.lower <= b.lower && a.upper <= b.upper && a.upper >= b.lower) {
			return i(b.lower, a.upper);
		}
		return i(a.lower, b.upper);
	}

	public static Interval difference(Interval a, Interval b) {
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
					if (max(x, x | (base - 1)) == Integer.MAX_VALUE)
						return result;
					begin = max(x, x | (base - 1)) + 1;
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
		int x = i1.lower + i2.lower;
		int y = i1.upper + i2.upper;
		return i(min(x, y), max(x, y));
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
		int x = i1.lower - i2.upper;
		int y = i1.upper - i2.lower;
		return i(min(x, y), max(x, y));
	}

	public static Interval multiply(Interval i1, Interval i2) {
		// FIXME imprecise
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (getOverflowType(i1.lower * i2.lower) != 0
				|| getOverflowType(i1.upper * i2.upper) != 0) {
			return top();
		}
		return i(i1.lower * i2.lower, i1.upper * i2.upper);
	}

	public static Interval or(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		Interval result = i();
		List<BinaryInterval> bi1 = i1.splitIntoBinary();
		List<BinaryInterval> bi2 = i2.splitIntoBinary();
		ListIterator<BinaryInterval> it1 = bi1.listIterator();
		// System.out.println("or "+i1+" , "+i2);
		// System.out.println(bi1);
		// System.out.println(bi2);
		while (it1.hasNext()) {
			BinaryInterval a = it1.next();
			int maska = ((1 << (32 - a.n)) - 1);
			ListIterator<BinaryInterval> it2 = bi2.listIterator();
			while (it2.hasNext()) {
				BinaryInterval b = it2.next();
				int maskb = ((1 << (32 - b.n)) - 1);
				int x1 = a.base | b.base;
				int x2 = a.base | b.base;
				// System.out.printf("\ta.n %d b.n %d\n", a.n, b.n);
				if (a.n > b.n) {
					x2 |= maskb;
				} else {
					x2 |= maska;
				}
				// System.out.printf("\tmaska %x maskb %x x1 %d x2 %d\n", maska,
				// maskb, x1, x2);
				result = union(result, i(min(x1, x2), max(x1, x2)));
			}
		}
		return result;
	}

	public static Interval and(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		Interval result = i();
		List<BinaryInterval> bi1 = i1.splitIntoBinary();
		List<BinaryInterval> bi2 = i2.splitIntoBinary();
		ListIterator<BinaryInterval> it1 = bi1.listIterator();
		// System.out.println("and "+i1+" , "+i2);
		while (it1.hasNext()) {
			BinaryInterval a = it1.next();
			int maska = ((1 << (32 - a.n)) - 1);
			ListIterator<BinaryInterval> it2 = bi2.listIterator();
			while (it2.hasNext()) {
				BinaryInterval b = it2.next();
				int maskb = ((1 << (32 - b.n)) - 1);
				int x1 = (a.base | maska) & (b.base | maskb);
				int x2 = a.base & b.base;
				// System.out.printf("\ta.n %d b.n %d\n", a.n, b.n);
				if (a.n > b.n) {
					x2 &= ~maskb;
				} else {
					x2 &= ~maska;
				}
				// System.out.printf("\tmaska %x maskb %x x1 %d x2 %d\n", maska,
				// maskb, x1, x2);
				result = union(result, i(min(x1, x2), max(x1, x2)));
			}
		}
		return result;
	}

	public static Interval xor(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		Interval result = i();
		List<BinaryInterval> bi1 = i1.splitIntoBinary();
		List<BinaryInterval> bi2 = i2.splitIntoBinary();
		ListIterator<BinaryInterval> it1 = bi1.listIterator();
		while (it1.hasNext()) {
			BinaryInterval a = it1.next();
			ListIterator<BinaryInterval> it2 = bi2.listIterator();
			while (it2.hasNext()) {
				BinaryInterval b = it2.next();
				int mn = min(b.n, a.n);
				int mask = ((1 << (32 - mn)) - 1);
				int x = (b.base & ~mask) ^ (a.base & ~mask);
				result = union(result, i(min(x | mask, x), max(x | mask, x)));
			}
		}
		return result;
	}

	public static Interval shl(Interval i1, Interval i2) {
		// FIXME
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		return i(0);
	}

	public static Interval shr(Interval i1, Interval i2) {
		// FIXME
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		return i(0);
	}

	public static Interval slr(Interval i1, Interval i2) {
		// FIXME
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		return i(0);
	}

	public static Interval divide(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (i2.contains(0)) {
			return top();
		}
		int divs[] = { i1.lower / i2.lower, i1.lower / i2.upper,
				i1.upper / i2.lower, i1.upper / i2.upper };
		Arrays.sort(divs);
		Interval result = i(divs[0], divs[3]);
		if (i1.contains(mi) && i2.contains(-1)) {
			result = union(result, mi);
		}
		return result;
	}

	public static Interval modulo(Interval i1, Interval i2) {
		// FIXME unsound & imprecise
		System.out.println("modulo "+i1+", "+i2);
		System.out.println("\t(1,-1): "+(i1.lower/i2.upper)+"\t(1,1): "+(i1.upper/i2.upper));
		System.out.println("\t(-1,-1): "+(i1.lower/i2.lower)+"\t(1,-1): "+(i1.upper/i2.lower));
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		if (i2.contains(0)) {
			return top();
		}
		if (i2.size() > 1) {
			return top();
		}
		int div = Math.abs(i2.lower);
		if (i1.size() > div) {
			return i(0, div - 1);
		}
		int x = i1.lower % div;
		int y = (int) ((x + i1.size() - 1) % div);
		if (y < x) {
			return i(0, div - 1);
		}
		return i(min(x, y), max(x, y));
	}
}
