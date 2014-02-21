package self.ec.btcbots.dao;

import self.ec.btcbots.entity.Account;
import self.ec.btcbots.entity.Transaction;
import self.ec.btcbots.entity.User;

public class DaoFactory {

	private DaoFactory() {}

	private static final GenericDao<User> userDao = new GenericDao<User>(User.class);
	private static final GenericDao<Account> accountDao = new GenericDao<Account>(Account.class);
	private static final GenericDao<Transaction> transactionDao = new GenericDao<Transaction>(Transaction.class);
	
	public static GenericDao<User> getUserDao() {
		return userDao;
	}
	public static GenericDao<Account> getAccountDao() {
		return accountDao;
	}
	public static GenericDao<Transaction> getTransactionDao() {
		return transactionDao;
	}
	
}