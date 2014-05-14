package self.ec.argume.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.glassfish.jersey.server.mvc.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.FacebookMeResponse;
import self.ec.argume.model.HtmlResponse;
import self.ec.argume.model.User;
import self.ec.argume.model.User.Source;
import self.ec.argume.util.AuthUtils;
import self.ec.argume.util.Constants;
import self.ec.argume.util.Messages;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class LoginResource {

	private static final Logger logger = LoggerFactory.getLogger(LoginResource.class);
	
	private static final GenericDao<User> userDao = DaoFactory.getUserDao();
	
	private static URI homepage = null;
	static {
		try {
			homepage = new URI("/"); 
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		} 
	}
	
	@Context
	HttpServletRequest request;
	
	@GET
	@Path("login")
    public Viewable index() 
	{
        return new Viewable("/login", new HtmlResponse(request));
    }
	
	@GET
    @Path("logout")
    public Response logout() 
	{
		HttpSession session = request.getSession(false);
		if (session != null) session.invalidate();
		
		return Response.status(Status.SEE_OTHER).location(homepage).build();
    }
	
	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("email") String email, @FormParam("password") String password) 
	{
		User authUser = AuthUtils.getAuthenticatedUser(email, password);
		if (authUser == null) {
			return Response.status(Status.UNAUTHORIZED).entity(
					new Viewable("/login", new HtmlResponse(Collections.singletonMap("errorMessage", 
									Messages.getMessage("errors.login.unauthenticated")), request))).build(); 			
		} 

		authUser.clearPassword();
		request.getSession().setAttribute("user", authUser);
		
		return Response.status(Status.SEE_OTHER).location(homepage).build();
    }
	
	@POST
	@Path("fb_login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response fbLogin(@FormParam("userId") String userId, @FormParam("accessToken") String accessToken) {
		FacebookMeResponse response = null;
		try {
			response = AuthUtils.getLoggedInFacebookUser(userId, accessToken);
			if (response != null) {
				// check to see if we have this user, otherwise first insert it into USERS
				User user = null;
				List<User> list = userDao.findBy("idAtSource", userId); 
				if (list != null && list.size() > 0) {
					user = list.get(0);
				}
				if (user == null) {
					user = new User();
					// assign dummy email and password
					user.setEmail("dummy" + userId + "@facebook.com");
					user.setPasswordSalt(RandomStringUtils.randomAlphabetic(32));
					user.setPasswordHash(DigestUtils.sha256Hex(RandomStringUtils.randomAlphanumeric(8).getBytes()));
					user.setFirstName(response.getFirstName());
					user.setLastName(response.getLastName());
					user.setSource(Source.FACEBOOK);
					user.setIdAtSource(userId);
					user.setImgUrl(String.format(Constants.FB_IMG_URL_TEMPLATE, userId));
					user.setDateCreated(System.currentTimeMillis());
					userDao.save(user);
				}
				user.clearPassword();
				request.getSession().setAttribute("user", user);
				
				return Response.status(Status.SEE_OTHER).location(homepage).build();
			} 
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		return Response.status(Status.UNAUTHORIZED).entity(
				new Viewable("/login", new HtmlResponse(Collections.singletonMap("errorMessage", 
								Messages.getMessage("errors.login.unauthenticated")), request))).build(); 
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
}
