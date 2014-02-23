package self.ec.btcbots.model;

import java.util.HashMap;
import java.util.Map;

public class BotState {

	public static final String RUNNING = "RUNNING";
	
	private String status;
	private float btcBalance;
	private float remainingBudget;
	private Map<String,Object> params = new HashMap<>();
	
	public BotState() {}
	
	public BotState(String status, float btcBalance, float remainingBudget, 
					Map<String,Object> params) {
		this.status = status;
		this.btcBalance = btcBalance;
		this.remainingBudget = remainingBudget;
		this.params = params;
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

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	public void addParam(String key, Object value) {
		params.put(key, value);
	}
}