package self.ec.btcbots.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import self.ec.btcbots.dao.DaoFactory;
import self.ec.btcbots.dao.GenericDao;
import self.ec.btcbots.entity.Account;
import self.ec.btcbots.model.Currency;
import self.ec.btcbots.model.MonetaryAmount;
import self.ec.btcbots.util.AuthUtils;
import self.ec.btcbots.util.ResourceUtils;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

	private static final GenericDao<Account> accountDao = DaoFactory.getAccountDao();
	
	@Context
	HttpServletRequest request;
	
	@GET
	public Account getAccountInfo() {
		Account account = ResourceUtils.getAccountForLoggedInUser(request);
		if (account != null) return account;
		
		throw new WebApplicationException(Response.Status.NOT_FOUND);
	}
	
	@POST
	@Path("deposit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deposit(MonetaryAmount amount) {
		if (Currency.USD != amount.getCurrency()) {
			throw new WebApplicationException(amount.getCurrency().name() 
					+ " is not supported yet", Response.Status.BAD_REQUEST);
		}
		Account account = ResourceUtils.getAccountForLoggedInUser(request);
		if (account == null) {
			account = new Account();
			account.setUserId(AuthUtils.getUserInSession(request).getId());
			account.setUsdBalance(amount.getValue());
			account.setDateCreated(System.currentTimeMillis());
		} else {
			account.setUsdBalance(account.getUsdBalance() + amount.getValue());
			account.setDateModified(System.currentTimeMillis());
		}
		
		accountDao.save(account);
		
		return Response.ok().build();
	}
	
	@POST
	@Path("withdraw")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response withdraw(MonetaryAmount amount) {
		if (Currency.USD != amount.getCurrency()) {
			throw new WebApplicationException(amount.getCurrency().name() 
					+ " is not supported yet", Response.Status.BAD_REQUEST);
		}
		Account account = ResourceUtils.getAccountForLoggedInUser(request);
		if (account == null) {
			throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
		} else if (account.getUsdBalance() < amount.getValue()) {
			throw new WebApplicationException("Withdrawal amount cannot be greater than the current balance",
					Response.Status.BAD_REQUEST); 
		} else {
			account.setUsdBalance(account.getUsdBalance() - amount.getValue());
			account.setDateModified(System.currentTimeMillis());
		}
		
		accountDao.save(account);
		
		return Response.ok().build();
	}
	
}