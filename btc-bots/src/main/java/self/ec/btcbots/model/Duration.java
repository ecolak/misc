package self.ec.btcbots.model;


public class Duration {

	private int interval;
	private TimeUnit timeUnit;
	
	public Duration(int interval, TimeUnit timeUnit) {
		this.interval = interval;
		this.timeUnit = timeUnit;
	}

	public int getInterval() {
		return interval;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
	
	public long toMillis() {
		return interval * timeUnit.getMillis();
	}
}
