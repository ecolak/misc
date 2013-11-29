package self.ec.argume.util;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.model.User;

public class AuthUtils {

	private AuthUtils() {}
	
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
	
	public static boolean isUserAdmin(String username, String password) {
		User user = getAuthenticatedUser(username, password);
		return ((user != null && user.isAdmin()) ? true : false);
	}
	
	public static boolean isUserInSessionAdmin(HttpServletRequest request) {
		User user = getUserInSession(request);
		return ((user != null && user.isAdmin()) ? true : false);
	}
	
	public static User getUserInSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return (session != null ? ((User) request.getSession().getAttribute("user")) : null);
	}
	
	public static String getVisitorIdFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if ("visitor_id".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
