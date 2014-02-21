package self.ec.btcbots.api;

public class Fee {
	public Price coinbase;
	public Price bank;
	
	public Fee () {}
	
	public Fee (Price coinbase, Price bank) {
		this.coinbase = coinbase;
		this.bank = bank;
	}
}
