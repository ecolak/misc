package self.ec.btcbots.bot;

import java.util.Map;

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
import self.ec.btcbots.model.BotState;
import self.ec.btcbots.simu.Constants;
import self.ec.btcbots.util.JobUtil;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class SimpleBot implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleBot.class);
	
	private static enum Mode {
		BUY, SELL
	}
	
	private static final GenericDao<Transaction> transactionDao = DaoFactory.getTransactionDao();
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobKey = context.getJobDetail().getKey().toString();
		JobDataMap jobData = context.getJobDetail().getJobDataMap();
			
		// mandatory parameters
		BotConfig config = null;
		if (jobData.containsKey("config")) {
			config = (BotConfig)jobData.get("config");
		}
		
		if (config == null) {
			throw new IllegalArgumentException("Job config not found");
		}
		
		Map<String,Object> configParams = (Map<String,Object>)config.getParams();
		if (configParams == null || configParams.isEmpty()) {
			throw new IllegalArgumentException("Invalid job configuration: No parameters");
		}
		
		LOG.info("Config params: " + configParams);
		
		float budget = JobUtil.getFloatFromParams(configParams, BotConfig.PARAM_BUDGET, 0);	
		if (budget <= 0) {
			throw new IllegalArgumentException("Invalid budget. Budget must be positive");
		}
		
		BotState state = null;
		if (jobData.containsKey("state")) {
			state = (BotState)jobData.get("state");
		} else {
			state = new BotState();
			jobData.put("state", state);
		}
		Map<String,Object> stateParams = (Map<String,Object>)state.getParams();
		Mode mode = Mode.valueOf(JobUtil.getStringFromParams(stateParams, "mode", Mode.BUY.name()));
		LOG.info(jobKey + " started executing. Mode: " + mode.name());
		
		// optional parameters
		Float startPrice = JobUtil.getFloatFromParams(configParams, BotConfig.PARAM_START_PRICE);	
		float tradeDiffPct = JobUtil.getFloatFromParams(configParams, BotConfig.PARAM_TRADE_DIFF_PCT, 
														BotConfig.DEFAULT_TRADE_DIFF_PCT);
		float minNumBtc = JobUtil.getFloatFromParams(configParams, BotConfig.PARAM_MIN_NUM_BTC, 
													 BotConfig.DEFAULT_MIN_NUM_BTC);
		float btcBalance = state.getBtcBalance();
		
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
						configParams.put(BotConfig.PARAM_START_PRICE, buyPrice);
						state.setBtcBalance(btcBalance + btcAmount);
					}
					
					stateParams.put("mode", Mode.SELL.name());
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
						state.setBtcBalance(btcBalance - btcAmount);
					}
					
					stateParams.put("mode", Mode.BUY.name());
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