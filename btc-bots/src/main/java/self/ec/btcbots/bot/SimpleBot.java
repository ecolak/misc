package self.ec.btcbots.bot;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import self.ec.btcbots.api.CoinbaseRestClient;
import self.ec.btcbots.api.Fee;
import self.ec.btcbots.api.PriceCheckResponse;
import self.ec.btcbots.api.TransactionResponse;
import self.ec.btcbots.api.TransactionResponse.Transfer;
import self.ec.btcbots.dao.DaoFactory;
import self.ec.btcbots.dao.GenericDao;
import self.ec.btcbots.entity.Transaction;
import self.ec.btcbots.model.BotConfig;
import self.ec.btcbots.simu.Constants;
import self.ec.btcbots.util.JobUtil;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class SimpleBot implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleBot.class);
	
	public static final float DEFAULT_MIN_NUM_BTC = 0.01f;
	public static final float DEFAULT_TRADE_DIFF_PCT = 5;
	
	private static enum Mode {
		BUY, SELL
	}
	
	private static final GenericDao<Transaction> transactionDao = DaoFactory.getTransactionDao();
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobKey = context.getJobDetail().getKey().toString();
		JobDataMap params = context.getJobDetail().getJobDataMap();
		
		Mode mode = Mode.valueOf(JobUtil.getStringFromJobDataMap(params, "mode", Mode.BUY.name()));
		LOG.info(jobKey + " started executing. Mode: " + mode.name());
			
		// mandatory parameters
		float budget = JobUtil.getFloatFromJobDataMap(params, BotConfig.PARAM_BUDGET, 0);	
		if (budget <= 0) {
			throw new IllegalArgumentException("Invalid budget. Budget must be positive");
		}
		
		// optional parameters
		Float startPrice = JobUtil.getFloatFromJobDataMap(params, BotConfig.PARAM_START_PRICE);
		
		float tradeDiffPct = JobUtil.getFloatFromJobDataMap(params, BotConfig.PARAM_TRADE_DIFF_PCT, 
															  DEFAULT_TRADE_DIFF_PCT);
		
		float minNumBtc = JobUtil.getFloatFromJobDataMap(params, "min_num_btc", DEFAULT_MIN_NUM_BTC);
		
		float btcBalance = JobUtil.getFloatFromJobDataMap(params, "btc_balance", 0);	
		
		if (Mode.BUY == mode) {
			PriceCheckResponse response = CoinbaseRestClient.getBuyPrice(1);
			if (response != null) {
				float buyPrice = Float.parseFloat(response.subtotal.amount);
				LOG.debug(String.format("buy price now: %f", buyPrice));
				
				float numBtc = getNumBtcToTrade(buyPrice, budget);
				LOG.debug(String.format("num btc to trade: %f", numBtc));
				
				if ((startPrice == null || buyPrice <= startPrice * (1-tradeDiffPct/100)) && (numBtc >= minNumBtc)) {
					LOG.info(String.format("Submitting buy order for %f btc at a price of %f USD", numBtc, buyPrice));
					// buy
					TransactionResponse transactionResponse = CoinbaseRestClient.simulateBuy(numBtc, buyPrice);		
					if (transactionResponse.success) {
						float btcAmount = saveTransaction(context.getJobDetail().getKey().toString(), transactionResponse);
						params.put(BotConfig.PARAM_START_PRICE, buyPrice);
						params.put("btc_balance", btcBalance + btcAmount);
					}
					
					params.put("mode", Mode.SELL.name());
				} else {
					LOG.debug(String.format("Do not buy. numBtc=%f, buyPrice=%f", numBtc, buyPrice));
				}
			}
		} else if (Mode.SELL == mode) {
			PriceCheckResponse response = CoinbaseRestClient.getSellPrice(1);
			if (response != null) {
				float sellPrice = Float.parseFloat(response.subtotal.amount);
				LOG.debug(String.format("sell price now: %f", sellPrice));
	
				if (sellPrice >= startPrice * (1+tradeDiffPct/100) && btcBalance > 0) {
					LOG.info(String.format("Submitting sell order for %f btc at a price of %f USD", btcBalance, sellPrice));
					// sell
					TransactionResponse transactionResponse = CoinbaseRestClient.simulateSell(btcBalance, sellPrice);
					if (transactionResponse.success) {
						float btcAmount = saveTransaction(context.getJobDetail().getKey().toString(), transactionResponse);				
						params.put("btc_balance", btcBalance - btcAmount);
					}
					
					params.put("mode", Mode.BUY.name());
				} else {
					LOG.debug(String.format("Do not sell. btcBalance=%f, sellPrice=%f", btcBalance, sellPrice));
				}
			}
		}
		
		try {
			Thread.sleep(5000); 
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		LOG.info(jobKey + " finished executing");
	}
	
	private float getNumBtcToTrade(float btcPrice, float budget) {
		return (budget - Constants.DEFAULT_BANK_FEE) / ((1+Constants.DEFAULT_TRANSACTION_PCT/100)*btcPrice);
	}
	
	private float saveTransaction(String jobKey, TransactionResponse transactionResponse) {
		Transfer transfer = transactionResponse.transfer;
		float btcAmount = Float.parseFloat(transfer.btc.amount);
		float btcPrice = Float.parseFloat(transfer.subtotal.amount) / btcAmount;
		float totalFees = 0;
		for (Fee fee : transfer.fees) {
			totalFees += Float.parseFloat(fee.bank.amount) + Float.parseFloat(fee.coinbase.amount);
		}
		transactionDao.save(new Transaction(jobKey, Transaction.Type.BUY, btcAmount, btcPrice, totalFees, System.currentTimeMillis()));
		
		return btcAmount;
	}
	
}