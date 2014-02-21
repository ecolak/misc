package self.ec.btcbots.api;

public class Price {

	// don't know why api doesn't return double
	public String amount;
	public String currency;
	
	public Price () {}

	public Price(String amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}

}