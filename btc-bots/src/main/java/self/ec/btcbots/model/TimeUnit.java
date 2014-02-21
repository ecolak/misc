package self.ec.btcbots.model;

public enum TimeUnit {

	DAYS(86400000), HOURS(3600000), MINUTES(60000), SECONDS(1000);
	
	private long millis;
	
	TimeUnit(long millis) {
		this.millis = millis;
	}
	
	public long getMillis() {
		return millis;
	}
}
