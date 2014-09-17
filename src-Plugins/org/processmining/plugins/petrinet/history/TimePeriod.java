package org.processmining.plugins.petrinet.history;

public class TimePeriod {
	private int start;
	private int end;
	public TimePeriod(int start, int end) {
		this.start=start;
		this.end=end;
	}
	
	public int getLength() {
		return end - start;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "TimePeriod [start=" + start + ", end=" + end + "]";
	}
}
