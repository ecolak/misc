package self.ec.btcbots.model;

public class BotState {

	public static final String RUNNING = "RUNNING";
	
	private String status;
	private float btcBalance;
	private float remainingBudget;
	
	public BotState() {}
	
	public BotState(String status, float btcBalance, float remainingBudget) {
		this.status = status;
		this.btcBalance = btcBalance;
		this.remainingBudget = remainingBudget;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public float getBtcBalance() {
		return btcBalance;
	}

	public void setBtcBalance(float btcBalance) {
		this.btcBalance = btcBalance;
	}

	public float getRemainingBudget() {
		return remainingBudget;
	}

	public void setRemainingBudget(float remainingBudget) {
		this.remainingBudget = remainingBudget;
	}
	
}