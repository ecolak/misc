package self.ec.argume.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.HashedPassword;
import self.ec.argume.model.PasswordChange;
import self.ec.argume.model.Signup;
import self.ec.argume.model.User;
import self.ec.argume.model.User.Source;
import self.ec.argume.model.UserDetails;
import self.ec.argume.util.AuthUtils;
import self.ec.argume.util.Messages;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private static final GenericDao<User> userDao = DaoFactory.getUserDao();
	
	@Context
	HttpServletRequest request;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(final Signup signup) {
		if (StringUtils.isBlank(signup.getEmail())) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(Messages.getMessage("errors.signup.blankEmail")).build());
		}
		
		if (!signup.getPassword().equals(signup.getPasswordConfirmation())) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(Messages.getMessage("errors.signup.passwordConfirmation")).build());
		}
		
		if (!signup.getPassword().matches(AuthUtils.PASSWORD_REGEX)) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(Messages.getMessage("errors.signup.weakPassword")).build());
		}
		
		List<User> users = userDao.findBy("email", signup.getEmail());
		if (!users.isEmpty()) {
			throw new WebApplicationException(Response.status(Status.CONFLICT)
					.entity(Messages.getMessage("errors.signup.emailExists")).build());
		}
		
		HashedPassword hp = AuthUtils.getHashedPassword(signup.getPassword());
		
		User user = new User(signup.getEmail(), hp.getHash(), hp.getSalt());
		user.setSource(Source.ARGUME);
		user.setDateCreated(System.currentTimeMillis());
		userDao.save(user).getId();
		
		return Response.status(Status.CREATED).build();
	}
	
	@PUT
	@Path("password")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePassword(PasswordChange passwordChange) {
		User userInSession = userDao.findById(AuthUtils.getUserInSession(request).getId());
		if (!AuthUtils.isCorrectPassword(userInSession, passwordChange.getOldPassword())) {
			throw new WebApplicationException(Response.status(Status.FORBIDDEN)
					.entity(Messages.getMessage("errors.updatepwd.oldPwdDoesNotMatch")).build());
		} 
		
		if (!passwordChange.getNewPassword().matches(AuthUtils.PASSWORD_REGEX)) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(Messages.getMessage("errors.signup.weakPassword")).build());
		}
		
		HashedPassword hp = AuthUtils.getHashedPassword(passwordChange.getNewPassword());
		userInSession.setPasswordHash(hp.getHash());
		userInSession.setPasswordSalt(hp.getSalt());
		userDao.save(userInSession);
		
		return Response.ok().build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUserDetails(UserDetails userDetails) {
		User userInSession = userDao.findById(AuthUtils.getUserInSession(request).getId());
		userInSession.setFirstName(userDetails.getFirstName());
		userInSession.setLastName(userDetails.getLastName());
		userInSession.setImgUrl(userDetails.getImgUrl());
		userDao.save(userInSession);
		request.getSession().setAttribute("user", userInSession);
		
		return Response.ok().build();
	}
	
}