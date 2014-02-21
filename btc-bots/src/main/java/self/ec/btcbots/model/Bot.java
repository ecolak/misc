package self.ec.btcbots.model;

public class Bot {

	private BotConfig config;
	private BotState state;
	
	public Bot() {}
	
	public Bot(BotConfig config, BotState state) {
		this.config = config;
		this.state = state;
	}

	public BotConfig getConfig() {
		return config;
	}

	public void setConfig(BotConfig config) {
		this.config = config;
	}

	public BotState getState() {
		return state;
	}

	public void setState(BotState state) {
		this.state = state;
	}
	
}