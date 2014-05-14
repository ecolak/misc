package self.ec.argume.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.server.mvc.Viewable;

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.HashedPassword;
import self.ec.argume.model.HtmlResponse;
import self.ec.argume.model.User;
import self.ec.argume.model.User.Source;
import self.ec.argume.util.AuthUtils;
import self.ec.argume.util.MapBuilder;
import self.ec.argume.util.Messages;

@Path("/signup")
@Produces(MediaType.TEXT_HTML)
public class SignupResource {

	private static final GenericDao<User> userDao = DaoFactory.getUserDao();
	
	@Context
	HttpServletRequest request;
	
	@GET
    public Viewable index() 
	{
        return new Viewable("/signup", new HtmlResponse(request));
    }
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Viewable create(@FormParam("email") String email, 
						   @FormParam("password") String password,
						   @FormParam("password_confirmation") String passwordConfirmation) {
		if (StringUtils.isBlank(email)) {
			return getErrorResponse("errors.signup.blankEmail");
		}
		
		if (!password.matches(AuthUtils.PASSWORD_REGEX)) {
			return getErrorResponse("errors.signup.weakPassword");
		}
		
		if (!password.equals(passwordConfirmation)) {
			return getErrorResponse("errors.signup.passwordConfirmation");
		}
				
		List<User> users = userDao.findBy("email", email);
		if (!users.isEmpty()) {
			return getErrorResponse("errors.signup.emailExists");
		}
		
		HashedPassword hp = AuthUtils.getHashedPassword(password);
		
		User user = new User(email, hp.getHash(), hp.getSalt());
		user.setSource(Source.ARGUME);
		user.setDateCreated(System.currentTimeMillis());
		userDao.save(user).getId();
		
		return getSuccessResponse("success.signup");
	}
	
	private Viewable getErrorResponse(String messageKey) {
		return getResponse(messageKey, "errorMessage");
	}
	
	private Viewable getSuccessResponse(String messageKey) {
		return getResponse(messageKey, "successMessage");
	}
	
	private Viewable getResponse(String messageKey, String messageType) {
		return new Viewable("/signup", new HtmlResponse(new MapBuilder<String,String>()
				.put(messageType, Messages.getMessage(messageKey))
				.build(), request));
	}
}
