package self.ec.btcbots.model;

public enum TimeUnit {

	DAY(86400000), HOUR(3600000), MINUTE(60000), SECOND(1000);
	
	private long millis;
	
	TimeUnit(long millis) {
		this.millis = millis;
	}
	
	public long getMillis() {
		return millis;
	}
}
