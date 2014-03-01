package self.ec.btcbots.dao;

import self.ec.btcbots.entity.Account;
import self.ec.btcbots.entity.BotOwner;
import self.ec.btcbots.entity.Transaction;
import self.ec.btcbots.entity.User;

public class DaoFactory {

	private DaoFactory() {}

	private static final GenericDao<User> userDao = new GenericDao<User>(User.class);
	private static final GenericDao<Account> accountDao = new GenericDao<Account>(Account.class);
	private static final GenericDao<Transaction> transactionDao = new GenericDao<Transaction>(Transaction.class);
	private static final GenericDao<BotOwner> botOwnerDao = new GenericDao<BotOwner>(BotOwner.class);
	
	public static GenericDao<User> getUserDao() {
		return userDao;
	}
	public static GenericDao<Account> getAccountDao() {
		return accountDao;
	}
	public static GenericDao<Transaction> getTransactionDao() {
		return transactionDao;
	}
	
	public static GenericDao<BotOwner> getBotOwnerDao() {
		return botOwnerDao;
	}
	
}