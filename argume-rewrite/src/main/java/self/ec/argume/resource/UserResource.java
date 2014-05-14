package self.ec.argume.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.mvc.Viewable;

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.HashedPassword;
import self.ec.argume.model.HtmlResponse;
import self.ec.argume.model.User;
import self.ec.argume.util.AuthUtils;
import self.ec.argume.util.MapBuilder;
import self.ec.argume.util.Messages;

@Path("/user")
@Produces("text/html")
public class UserResource {

	private static final GenericDao<User> userDao = DaoFactory.getUserDao();
	
	@Context
	HttpServletRequest request;
	
	@GET
    public Viewable index() 
	{
        return new Viewable("/user_profile", new HtmlResponse(request));
    }
	
	@POST
    public Viewable updateDetails(@FormParam("first_name") String firstName,
    							  @FormParam("last_name") String lastName,
    							  @FormParam("img_url") String imgUrl) 
	{
		User userInSession = userDao.findById(AuthUtils.getUserInSession(request).getId());
		userInSession.setFirstName(firstName);
		userInSession.setLastName(lastName);
		userInSession.setImgUrl(imgUrl);
		userDao.save(userInSession);
		request.getSession().setAttribute("user", userInSession);
		
		return getSuccessResponse("success.profileUpdate");
    }
	
	@POST
	@Path("password")
    public Viewable updatePassword(@FormParam("old_password") String oldPassword,
    							   @FormParam("new_password") String newPassword,
    							   @FormParam("new_password_confirmation") String newPasswordConfirmation) 
	{
		User userInSession = userDao.findById(AuthUtils.getUserInSession(request).getId());
		if (!AuthUtils.isCorrectPassword(userInSession, oldPassword)) {
			return getErrorResponse("errors.updatepwd.oldPwdDoesNotMatch");
		} 
		
		if (newPassword == null || !newPasswordConfirmation.matches(AuthUtils.PASSWORD_REGEX)) {
			return getErrorResponse("errors.signup.weakPassword");
		}
		
		if (!newPassword.equals(newPasswordConfirmation)) {
			return getErrorResponse("errors.signup.passwordConfirmation");
		}
		
		HashedPassword hp = AuthUtils.getHashedPassword(newPassword);
		userInSession.setPasswordHash(hp.getHash());
		userInSession.setPasswordSalt(hp.getSalt());
		userDao.save(userInSession);
		
		return getSuccessResponse("success.passwordUpdate");
    }
	
	private Viewable getErrorResponse(String messageKey) {
		return getResponse(messageKey, "alert-danger");
	}
	
	private Viewable getSuccessResponse(String messageKey) {
		return getResponse(messageKey, "alert-success");
	}
	
	private Viewable getResponse(String messageKey, String cssClass) {
		return new Viewable("/user_profile", new HtmlResponse(new MapBuilder<String,String>()
				.put("updateUserProfileMessage", Messages.getMessage(messageKey))
				.put("updateUserProfileMessageCssClass", cssClass).build(), request));
	}
	
}