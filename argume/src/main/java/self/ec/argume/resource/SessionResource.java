package self.ec.argume.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.FacebookLogin;
import self.ec.argume.model.FacebookMeResponse;
import self.ec.argume.model.Login;
import self.ec.argume.model.User;
import self.ec.argume.model.User.Source;
import self.ec.argume.util.AuthUtils;

@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {
	
	private static final GenericDao<User> userDao = DaoFactory.getUserDao();
	
	@Context
	HttpServletRequest request;
	
	@GET
	@Path("user")
	public User getSessionUser() {
		User user = (User) request.getSession().getAttribute("user");
		if (user != null) user.clearPassword();
		return user;
	}
	
	@DELETE
	public Response logout() {
		HttpSession session = request.getSession(false);
		if (session != null) session.invalidate(); 
		
		return Response.ok().build();
	}
	
	@POST
	@Path("fb_logout")
	public Response fbLogout() {
		HttpSession session = request.getSession(false);
		if (session != null) {
			User user = (User) session.getAttribute("user");
			if (user != null && Source.FACEBOOK == user.getSource()) {
				session.invalidate(); 
				return Response.ok().build();
			} 
		}
		
		return Response.status(Status.NOT_FOUND).build();
	}
	
	@POST
	public User login(final Login login) {
		User authUser = AuthUtils.getAuthenticatedUser(login.getEmail(), login.getPassword());
		if (authUser == null) {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);			
		} 

		authUser.clearPassword();
		request.getSession().setAttribute("user", authUser);
		return authUser;
	}
	
	@POST
	@Path("fb_login")
	public User fbLogin(final FacebookLogin fbLogin) {
		FacebookMeResponse response = null;
		User loggedInUser = null;
		try {
			response = AuthUtils.getLoggedInFacebookUser(fbLogin.getUserId(), fbLogin.getAccessToken());
			if (response != null) {
				// check to see if we have this user, otherwise first insert it into USERS
				User user = null;
				List<User> list = userDao.findBy("idAtSource", String.valueOf(fbLogin.getUserId())); 
				if (list != null && list.size() > 0) {
					user = list.get(0);
				}
				if (user == null) {
					user = new User();
					// assign dummy email and password
					user.setEmail("dummy" + fbLogin.getUserId() + "@facebook.com");
					user.setPasswordSalt(RandomStringUtils.randomAlphabetic(32));
					user.setPasswordHash(DigestUtils.sha256Hex(RandomStringUtils.randomAlphanumeric(8).getBytes()));
					user.setFirstName(response.getFirstName());
					user.setLastName(response.getLastName());
					user.setSource(Source.FACEBOOK);
					user.setIdAtSource(fbLogin.getUserId());
					user.setDateCreated(System.currentTimeMillis());
					userDao.save(user);
				}
				user.clearPassword();
				request.getSession().setAttribute("user", user);
				loggedInUser = user;
			} else {
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
			} 
		} catch (IOException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return loggedInUser;
	}
	
}