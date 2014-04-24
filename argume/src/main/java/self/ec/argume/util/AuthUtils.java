package self.ec.argume.util;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.model.FacebookMeResponse;
import self.ec.argume.model.HashedPassword;
import self.ec.argume.model.User;

public class AuthUtils {

	private AuthUtils() {}
	
	public static final String PASSWORD_REGEX = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
	
	private static final Client jerseyClient = ClientBuilder.newClient();
	private static final ObjectMapper om = new ObjectMapper();
	
	public static User getAuthenticatedUser(String email, String password) {
		User result = null;
		List<User> list = DaoFactory.getUserDao().findBy("email", email);
		if (!list.isEmpty()) {
			User user = list.get(0);
			if (isCorrectPassword(user, password)) {
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
	
	public static FacebookMeResponse getLoggedInFacebookUser(String fbUserId, String accessToken) throws IOException {	
		FacebookMeResponse result = null;
		WebTarget fbAccessTokenTarget = jerseyClient.target(Constants.FB_GRAPH_API_BASE).path("me");
		Response response = fbAccessTokenTarget.queryParam("access_token", 
							accessToken).request().get();
		if (Status.OK.getStatusCode() == response.getStatus()) {			
			JsonNode root = om.readTree(JerseyUtils.getEntityFromResponse(response));
			if (root != null) {
				String fbuid = root.get("id").getTextValue();
				if (fbUserId.equals(fbuid)) {
					result = new FacebookMeResponse();
					if (root.get("id") != null) {
						result.setUserId(root.get("id").getTextValue());
					}
					if (root.get("first_name") != null) {
						result.setFirstName(root.get("first_name").getTextValue());
					}
					if (root.get("last_name") != null) {
						result.setLastName(root.get("last_name").getTextValue());
					} 
				}
			}
		}
		
		return result;
	}
	
	public static HashedPassword getHashedPassword(String password) {
		String passwordSalt = RandomStringUtils.randomAlphabetic(32);
		String saltedPassword = passwordSalt + password;
		String passwordHash = DigestUtils.sha256Hex(saltedPassword.getBytes());
		return new HashedPassword(passwordHash, passwordSalt);
	}
	
	public static boolean isCorrectPassword(User user, String password) {
		if (user != null && password != null) {
			String salt = user.getPasswordSalt();
			String saltedPassword = salt + password;
			String saltedHash = DigestUtils.sha256Hex(saltedPassword);
			if (saltedHash.equals(user.getPasswordHash())) {
				return true; 
			}
		}
		return false;
	}
}
