package ch.ethz.sae;
import java.util.HashMap;
import java.util.Map;

public class IntervalPerVar {
	private IntervalPerVar(boolean bottom) {
		this.bottom = bottom;
		values = new HashMap<String, Interval>();
	}
	
	public IntervalPerVar() {
		this.bottom = false;
		values = new HashMap<String, Interval>();
	}
	
	@Override
	public String toString() {
		if (bottom)
			return "bottom";
		StringBuilder b = new StringBuilder();
		for (Map.Entry<String, Interval> entry : values.entrySet()) {
			if (b.length() != 0) b.append(", ");
			b.append(entry.getKey());
			b.append("=");
			b.append(entry.getValue().toString());
		}		
		return b.toString();
	}	
	
	// This does deep copy of values as opposed to shallow copy, but feel free to optimize.
	public void copyFrom(IntervalPerVar other) {
		values.clear();
		for (Map.Entry<String, Interval> entry : other.values.entrySet()) {
			values.put(entry.getKey(), entry.getValue());
		}
		bottom = other.bottom;
	}
	
	public void merge(IntervalPerVar a, IntervalPerVar b) {
		values.clear();
		for (Map.Entry<String, Interval> entry : a.values.entrySet()) {
			if (b.values.containsKey(entry.getKey())) {
				values.put(entry.getKey(), Interval.union(entry.getValue(), b.values.get(entry.getKey())));
			} else {
				values.put(entry.getKey(), entry.getValue());
			}
		}
		for (Map.Entry<String, Interval> entry : b.values.entrySet()) {
			if (!values.containsKey(entry.getKey())) {
				values.put(entry.getKey(), entry.getValue());
			}
		}
		bottom = a.bottom && b.bottom;
	}
	
	public void mergeInto(IntervalPerVar src) {
		if (src.isBottom()) {
			return;
		}
		if (isBottom()) {
			copyFrom(src);
			return;
		}
		
		for (Map.Entry<String, Interval> entry : src.values.entrySet()) {
			if (values.containsKey(entry.getKey())) {
				values.put(entry.getKey(), Interval.union(entry.getValue(), values.get(entry.getKey())));
			} else {
				values.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public void widen(IntervalPerVar a, IntervalPerVar b) {
		values.clear();
		for (Map.Entry<String, Interval> entry : a.values.entrySet()) {
			if (b.values.containsKey(entry.getKey())) {
				values.put(entry.getKey(), Interval.widen(entry.getValue(), b.values.get(entry.getKey())));
			} else {
				values.put(entry.getKey(), entry.getValue());
			}
		}
		for (Map.Entry<String, Interval> entry : b.values.entrySet()) {
			if (!values.containsKey(entry.getKey())) {
				values.put(entry.getKey(), entry.getValue());
			}
		}
		System.err.println("widened:\n\t"+a+"\n\t"+b+"\n\t"+this);
		System.exit(1);
		bottom = a.bottom && b.bottom;
	}
	
	public void widenInto(IntervalPerVar src) {
		if (src.isBottom()) {
			return;
		}
		if (isBottom()) {
			copyFrom(src);
			return;
		}
		
		System.err.print("widenedInto:\n\t"+this);
		for (Map.Entry<String, Interval> entry : src.values.entrySet()) {
			if (values.containsKey(entry.getKey())) {
				values.put(entry.getKey(), Interval.union(entry.getValue(), values.get(entry.getKey())));
			} else {
				values.put(entry.getKey(), entry.getValue());
			}
		}
		System.err.println("\n\t"+src+"\n\t"+this);
		System.exit(1);
	}
	
	void putIntervalForVar(String var, Interval i) {
		values.put(var, i);
	}
	
	Interval getIntervalForVar(String var) {
		return values.get(var);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bottom ? 1231 : 1237);
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IntervalPerVar)) return false;
		if (bottom != ((IntervalPerVar)o).bottom) return false;
		return ((IntervalPerVar)o).values.equals(values);
	}
	
	public boolean isBottom() {
		return bottom;
	}
	
	public static IntervalPerVar bottom() {
		return new IntervalPerVar(true);
	}
	
	private HashMap<String, Interval> values;
	private boolean bottom;
}
