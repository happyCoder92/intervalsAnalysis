package ch.ethz.sae;

public class IntervalHelper {
	public final static int ma = Integer.MAX_VALUE;
	public final static int mi = Integer.MIN_VALUE;
	
	public static int b(int... b) {
		int result = 0;
		for (int i = 0; i < b.length; ++i) {
			result |= 1 << b[i];
		}
		return result;
	}
	
	public static Interval i() {
		return new Interval();
	}
	
	public static Interval i(int i) {
		return new Interval(i);
	}
	
	public static Interval i(int a, int b) {
		return new Interval(a, b);
	}
}
