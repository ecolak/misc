package self.ec.btcbots.model;


public class PointInTime {

	private boolean now;
	private Duration delay;
	private Long timeInMillis;
	
	public boolean isNow() {
		return now;
	}
	public void setNow(boolean now) {
		this.now = now;
	}
	public Duration getDelay() {
		return delay;
	}
	public void setDelay(Duration delay) {
		this.delay = delay;
	}
	public Long getTimeInMillis() {
		return timeInMillis;
	}
	public void setTimeInMillis(Long timeInMillis) {
		this.timeInMillis = timeInMillis;
	} 
}
