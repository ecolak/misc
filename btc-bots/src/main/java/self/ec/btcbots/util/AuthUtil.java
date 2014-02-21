package self.ec.btcbots.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;

import self.ec.btcbots.dao.DaoFactory;
import self.ec.btcbots.entity.User;

public class AuthUtil {

	private AuthUtil() {}
	
	public static final String PASSWORD_REGEX = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
	
	public static User getAuthenticatedUser(String email, String password) {
		User result = null;
		List<User> list = DaoFactory.getUserDao().findBy("email", email);
		if (!list.isEmpty()) {
			User user = list.get(0);
			String salt = user.getPasswordSalt();
			String saltedPassword = salt + password;
			String saltedHash = DigestUtils.sha256Hex(saltedPassword);
			if (saltedHash.equals(user.getPasswordHash())) {
				result = user;
			}
		}
		
		return result;
	}
	
	public static User getUserInSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return (session != null ? ((User) request.getSession().getAttribute("user")) : null);
	}
	
}