package self.ec.btcbots.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import self.ec.btcbots.dao.DaoFactory;
import self.ec.btcbots.dao.GenericDao;
import self.ec.btcbots.entity.Account;

public class ResourceUtils {

  private static final GenericDao<Account> accountDao = DaoFactory.getAccountDao();

  private ResourceUtils() {}

  public static Account getAccountForLoggedInUser(HttpServletRequest request) {
    List<Account> accounts =
        accountDao.findBy("userId", AuthUtils.getUserInSession(request).getId());
    if (!accounts.isEmpty()) {
      return accounts.get(0);
    }
    return null;
  }
}
