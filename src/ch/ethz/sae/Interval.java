package ch.ethz.sae;

public class Interval {
	enum Tristate {
		ALWAYS_TRUE,
		PLAUSIBLE,
		ALWAYS_FALSE
	};
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
	
	public static Interval plus(Interval i1, Interval i2) {
		if (isPlusOverflow(i1.upper, i2.upper) != isPlusOverflow(i1.lower, i2.lower)) {
			return new Interval(Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
		int x = i1.lower + i2.lower;
		int y = i1.upper + i2.upper;
		return new Interval(Math.min(x, y), Math.max(x, y));
	}
	
	public static Interval minus(Interval i) {
		if (i.lower == Integer.MIN_VALUE && i.upper != Integer.MIN_VALUE) {
			return new Interval(Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
		return new Interval(-i.upper, -i.lower);
	}
	
	public static Interval minus(Interval i1, Interval i2) {
		if (isMinusOverflow(i1.upper, i2.lower) != isMinusOverflow(i1.lower, i2.upper)) {
			return new Interval(Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
		int x = i1.lower - i2.upper;
		int y = i1.upper - i2.lower;
		return new Interval(Math.min(x, y), Math.max(x, y));
	}
	
	public static Interval modulo(Interval i1, Interval i2) {
		int div = Math.abs(i2.lower);
		if (i1.size() > div) {
			return new Interval(0, div-1);
		}
		int x = i1.lower%div;
		int y = (int) ((x+i1.size()-1)%div);
		if (y < x) {
			return new Interval(0, div-1);
		}
		return new Interval(Math.min(x, y), Math.max(x, y));
	}
	
	public static int isPlusOverflow(int a, int b) {
		if ((long)a+b > Integer.MAX_VALUE)
			return 1;
		if ((long)a+b < Integer.MIN_VALUE)
			return -1;
		return 0;
	}
	
	public static int isMinusOverflow(int a, int b) {
		if ((long)a-b > Integer.MAX_VALUE)
			return 1;
		if ((long)a-b < Integer.MIN_VALUE)
			return -1;
		return 0;
	}
	
	public long size() {
		return (long)upper-lower+1;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Interval)) return false;
		Interval i = (Interval)o;
		return lower == i.lower && upper == i.upper;
	}

	// TODO: Do you need to handle infinity or empty interval?
	private final int lower, upper;
}
