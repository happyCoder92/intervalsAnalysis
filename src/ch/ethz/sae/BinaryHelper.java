package ch.ethz.sae;

import static ch.ethz.sae.IntervalHelper.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.List;

public class BinaryHelper {
	public static int orMax(int l1, int u1, int l2, int u2) {
		int b = b(30);
		for (int i = 1; i < 32; ++i, b >>>= 1) {
			if ((u1&b) == (u2&b)) {
				// x 1 x 1
				// 0 0 0 0
				if ((l1&b) != (u1&b) || (l2&b) != (u2&b)) {
					return u1|(b-1);
				}
			} else if ((u1&b) == 0) {
				// 0 0 x 1
				if ((l2&b) == 0) {
					// 0 0 0 1
					l2 = u2&(~(b-1)); // l2 = 10...
				}
				l1 ^= b; u1 ^= b;
			} else {
				// x 1 0 0
				if ((l1&b) == 0) {
					// 0 1 0 0
					l1 = u1&(~(b-1)); // l1 = 10...
				}
				l2 ^= b; u2 ^= b;
			}
		}
		return u1;
	}
	
	public static int orMin(int l1, int u1, int l2, int u2) {
		int b = b(30);
		for (int i = 1; i < 32; ++i, b >>>= 1) {
			if ((u1&b) == (u2&b)) {
				// x 1 x 1
				// 0 0 0 0
				if ((l1&b) != (u1&b) || (l2&b) != (u2&b)) {
					return max(l1, l2);
				}
			} else if ((u1&b) == 0) {
				// 0 0 x 1
				if ((l2&b) == 0) {
					// 0 0 0 1
					u2 = l2|(b-1); // u2 = 01...
				} else {
					l1 ^= b; u1 ^= b;
				}
			} else {
				// x 1 0 0
				if ((l1&b) == 0) {
					// 0 1 0 0
					u1 = l1|(b-1); // u1 = 01...
				} else {
					l2 ^= b; u2 ^= b;
				}
			}
		}
		return l1;
	}
	
	public static int andMax(int l1, int u1, int l2, int u2) {
		int b = b(30);
		for (int i = 1; i < 32; ++i, b >>>= 1) {
			if ((l1&b) == (l2&b)) {
				// 1 1 1 1
				// 0 x 0 x
				if ((l1&b) != (u1&b) || (l2&b) != (u2&b)) {
					return min(u1, u2);
				}
			} else if ((l2&b) == 0) {
				// 1 1 0 x
				if ((u2&b) == 0) {
					// 1 1 0 0
					l1 ^= b; u1 ^= b;
				} else {
					// 1 1 0 1
					l2 = u2&(~(b-1)); // l2 = 10...
				}
			} else {
				// 0 x 1 1
				if ((u1&b) == 0) {
					// 0 0 1 1
					l2 ^= b; u2 ^= b;
				} else {
					// 0 1 1 1
					l1 = u1&(~(b-1)); // l1 = 10...
				}
			}
		}
		return u1;
	}
	
	public static int andMin(int l1, int u1, int l2, int u2) {
		int b = b(30);
		for (int i = 1; i < 32; ++i, b >>>= 1) {
			if ((l1&b) == (l2&b)) {
				// 1 1 1 1
				// 0 x 0 x
				if ((l1&b) != (u1&b) || (l2&b) != (u2&b)) {
					return l1&(~(b-1));
				}
			} else if ((l2&b) == 0) {
				// 1 1 0 x
				if ((u2&b) != 0) {
					// 1 1 0 1
					u2 = l2|(b-1); // u2 = 01...
				}
				l1 ^= b; u1 ^= b;
			} else {
				// 0 x 1 1
				if ((u1&b) != 0) {
					// 0 1 1 1
					u1 = l1|(b-1); // u1 = 01...
				}
				l2 ^= b; u2 ^= b;
			}
		}
		return l1;
	}
	
	public static int xorMax(int l1, int u1, int l2, int u2) {
		int b = b(30);
		for (int i = 1; i < 32; ++i, b >>>= 1) {
			if ((l1&b) != (u1&b)) {
				if ((l2&b) != (u2&b)) {
					// 0 1 0 1
					return u1|(b-1);
				}
				if ((u2&b) == 0) {
					// 0 1 0 0
					l1 = u1&(~(b-1)); // l1 = 10...
					l2 ^= b; u2^= b;
				} else {
					// 0 1 1 1
					u1 = l1|(b-1); // u1 = 01...
					l1 ^= b; u1 ^= b;
				}
			} else if ((l2&b) != (u2&b)) {
				if ((u1&b) == 0) {
					// 0 0 0 1
					l2 = u2&(~(b-1)); // l2 = 10...
					l1 ^= b; u1^= b;
				} else {
					// 1 1 0 1
					u2 = l2|(b-1); // u2 = 01...
					l2 ^= b; u2 ^= b;
				}
			} else {
				if ((l1&b) != 0) {
					// 1 1 a a
					if ((l2&b) != 0) {
						// 1 1 1 1
						l1 ^= b; u1 ^= b;
					}
					l2 ^= b; u2 ^= b;
				} else if ((l2&b) != 0) {
					// 0 0 1 1
					l1 ^= b; u1 ^= b;
				}
			}
		}
		return u1;
	}
	
	public static int xorMin(int l1, int u1, int l2, int u2) {
		int b = b(30);
		for (int i = 1; i < 32; ++i, b >>>= 1) {
			if ((l1&b) != (u1&b)) {
				if ((l2&b) != (u2&b)) {
					// 0 1 0 1
					return l1&(~(b-1));
				}
				if ((u2&b) == 0) {
					// 0 1 0 0
					u1 = l1|(b-1); // u1 = 01...
				} else {
					// 0 1 1 1
					l1 = u1&(~(b-1)); // l1 = 10...
					l1 ^= b; u1 ^= b;
					l2 ^= b; u2 ^= b;
				}
			} else if ((l2&b) != (u2&b)) {
				if ((u1&b) == 0) {
					// 0 0 0 1
					u2 = l2|(b-1); // u2 = 01...
				} else {
					// 1 1 0 1
					l2 = u2&(~(b-1)); // l2 = 10...
					l1 ^= b; u1 ^= b;
					l2 ^= b; u2 ^= b;
				}
			} else {
				if ((l1&b) != 0) {
					// 1 1 a a
					if ((l2&b) != 0) {
						// 1 1 1 1
						l1 ^= b; u1 ^= b;
					}
					l2 ^= b; u2 ^= b;
				} else if ((l2&b) != 0) {
					// 0 0 1 1
					l1 ^= b; u1 ^= b;
				}
			}
		}
		return l1;
	}
	
	public static List<Interval> shiftIntervals(Interval i) {
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
	
	public static Interval shiftInterval(Interval i) {
		Interval result = i();
		for (Interval si : shiftIntervals(i)) {
			result = Interval.union(result, si);
		}
		return result;
	}
	
	public static int log2(int i) {
		int n = 0;
		while (i != 0) {
			i >>>= 1;
			++n;
		}
		return n;
	}
	
	public static Interval shlPositive(int l1, int u1, int l2, int u2) {
		int min = l1 << l2;
		int max = mi;
		int ru = 32-log2(u1);
		int bu = mi>>>ru;
		if (u2 < ru) {
			return i(l1 << l2, u1 << u2);
		}
		if ((l1&bu) == 0) {
			if (l2 <= ru) {
				return i(mi, (bu-1) << ru);
			}
			// min loop
			for (int i = max(l2, ru+1); i <= u2; ++i) {
				int b = mi>>>i;
				if (u1 >= (u1&(~(bu-1))|b) || l1 <= (l1|(bu-1)&(~(b-1)))) {
					min = mi;
					break;
				} 
				min = min(min, (l1|b)<<i);
			}
			// max loop
			for (int i = max(l2, ru+1); i <= u2; ++i) {
				int b = mi>>>i;
				if (u1 >= (u1&(~(bu-1))|(b-1)) || l1 <= (l1|(bu-1)^b)) {
					return i(min, (b-1) << i);
				} 
				max = max(max, u1<<i);
			}
		} else {
			if (l2 <= ru) {
				min = l1 << ru;
				if (l2 <= ru-1) {
					max = u1 << (ru-1);
				} else {
					max = u1 << ru;
				}
			}
			// min loop
			for (int i = max(l2, ru+1); i <= u2; ++i) {
				int b = mi>>>i;
				if ((l1&(~(b-1))) != (u1&(~(b-1)))) {
					if (u1 >= (l1&(~(b-1))|b|(b<<1)) || l1 <= (l1&(~(b-1))|b)) {
						min = mi;
						break;
					}
				}
				min = min(min, l1<<i);
			}
			// max loop
			for (int i = max(l2, ru+1); i <= u2; ++i) {
				int b = mi>>>i;
				if ((l1&(~(b-1))) != (u1&(~(b-1)))) {
					return i(min, (b-1) << i);
				}
				max = max(max, u1<<i);
			}
		}
		return i(min, max);
	}
}
