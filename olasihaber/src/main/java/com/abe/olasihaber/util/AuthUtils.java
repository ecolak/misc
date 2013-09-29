package com.abe.olasihaber.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;

import com.abe.olasihaber.dao.DaoFactory;
import com.abe.olasihaber.model.User;
import com.abe.olasihaber.model.UserCredentials;

public class AuthUtils {

	private AuthUtils() {}
	
	public static User getAuthenticatedUser(String username, String password) {
		User result = null;
		List<User> list = DaoFactory.getUserDao().findBy("username", username);
		if (!list.isEmpty()) {
			User user = list.get(0);
			UserCredentials userCred = DaoFactory.getUserCredentialsDao().findById(user.getId());
			if (userCred != null) {
				String salt = userCred.getSalt();
				String saltedPassword = salt + password;
				String saltedHash = DigestUtils.sha256Hex(saltedPassword);
				if (saltedHash.equals(userCred.getPassword())) {
					result = user;
				}
			}
		}
		
		return result;
	}
	
	public static boolean isUserAdmin(String username, String password) {
		User user = getAuthenticatedUser(username, password);
		return ((user != null && user.isAdmin()) ? true : false);
	}
	
	public static boolean isUserInSessionAdmin(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("session-user");
		return ((user != null && user.isAdmin()) ? true : false);
	}
}
