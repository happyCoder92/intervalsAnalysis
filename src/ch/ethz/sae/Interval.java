package ch.ethz.sae;

import java.util.Arrays;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static ch.ethz.sae.IntervalHelper.*;

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
		return (long)upper-lower+1;
	}
	
	public boolean contains(Interval a) {
		return a.isEmpty() || (!isEmpty() && lower <= a.lower && upper >= a.upper);
	}
	
	public boolean contains(int i) {
		return !isEmpty() && lower <= i && upper >= i; 
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Interval)) return false;
		Interval i = (Interval)o;
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
			return a;
		}
		if (b.isEmpty()) {
			return b;
		}
		return i(min(a.lower, b.lower), max(a.upper, b.upper));
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
		if (getOverflowType((long)i1.upper+i2.upper) != getOverflowType((long)i1.lower+i2.lower)) {
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
		if (getOverflowType((long)i1.upper-i2.lower) != getOverflowType((long)i1.lower-i2.upper)) {
			return i(mi, ma);
		}
		int x = i1.lower - i2.upper;
		int y = i1.upper - i2.lower;
		return i(min(x, y), max(x, y));
	}
	
	public static Interval multiply(Interval i1, Interval i2) {
		// FIXME
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		return i(i1.lower*i2.lower, i1.upper*i2.upper);
	}
	
	public static Interval or(Interval i1, Interval i2) {
		// FIXME
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		return i(0);
	}
	
	public static Interval and(Interval i1, Interval i2) {
		// FIXME
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		return i(0);
	}
	
	public static Interval xor(Interval i1, Interval i2) {
		// FIXME
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		return i(0);
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
		int divs[] = {i1.lower/i2.lower, i1.lower/i2.upper,
				i1.upper/i2.lower, i1.upper/i2.upper};
		Arrays.sort(divs);
		Interval result = i(divs[0], divs[3]); 
		if (i1.contains(mi) && i2.contains(-1)) {
			result = union(result, mi);
		}
		return result;
	}
	
	public static Interval modulo(Interval i1, Interval i2) {
		if (i1.isEmpty() || i2.isEmpty()) {
			throw new IllegalArgumentException("intervals cannot be empty");
		}
		int div = Math.abs(i2.lower);
		if (i1.size() > div) {
			return i(0, div-1);
		}
		int x = i1.lower%div;
		int y = (int) ((x+i1.size()-1)%div);
		if (y < x) {
			return i(0, div-1);
		}
		return i(min(x, y), max(x, y));
	}
}
