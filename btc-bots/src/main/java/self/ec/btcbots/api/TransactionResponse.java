package self.ec.btcbots.api;

public class TransactionResponse {

	public static class Transfer {
		public String type;
		public String code;
		public String created_at;
		public Fee[] fees;
		public String status;
		public String payout_date;
		public Price btc;
		public Price subtotal;
		public Price total;
	}
	
	public boolean success;
	public String[] errors;
	public Transfer transfer;
	
}
