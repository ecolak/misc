package self.ec.btcbots.model;


public class BotConfig {

	public static final String PARAM_BUDGET = "budget";
	public static final String PARAM_START_PRICE = "start_price";
	public static final String PARAM_TRADE_DIFF_PCT = "trade_diff_pct";
	
	private String name;
	private BotType botType;
	private Schedule schedule;
	private float budget;
	private Float startPrice;
	private Float tradeDiffPct;
	
	public BotConfig() {}
	
	public BotConfig(String name, BotType botType, Schedule schedule, 
			float budget, Float startPrice, Float tradeDiffPct) {
		this.name = name;
		this.botType = botType;
		this.schedule = schedule;
		this.budget = budget;
		this.startPrice = startPrice;
		this.tradeDiffPct = tradeDiffPct;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BotType getBotType() {
		return botType;
	}

	public void setBotType(BotType botType) {
		this.botType = botType;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public float getBudget() {
		return budget;
	}

	public void setBudget(float budget) {
		this.budget = budget;
	}

	public Float getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(Float startPrice) {
		this.startPrice = startPrice;
	}

	public Float getTradeDiffPct() {
		return tradeDiffPct;
	}

	public void setTradeDiffPct(Float tradeDiffPct) {
		this.tradeDiffPct = tradeDiffPct;
	}

}