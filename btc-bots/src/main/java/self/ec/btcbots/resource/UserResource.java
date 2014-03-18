package self.ec.btcbots.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import org.apache.commons.lang.StringUtils;

import self.ec.btcbots.dao.DaoFactory;
import self.ec.btcbots.dao.GenericDao;
import self.ec.btcbots.entity.User;
import self.ec.btcbots.model.MapBuilder;
import self.ec.btcbots.model.Signup;
import self.ec.btcbots.util.AuthUtils;

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
					.entity("Email cannot be blank").build());
		}
		
		if (!signup.getPassword().matches(AuthUtils.PASSWORD_REGEX)) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity("Password is too weak").build());
		}
		
		List<User> users = userDao.findBy("email", signup.getEmail());
		if (!users.isEmpty()) {
			throw new WebApplicationException(Response.status(Status.CONFLICT)
					.entity("An account with this email address already exists").build());
		}
		
		String passwordSalt = RandomStringUtils.randomAlphabetic(32);
		String saltedPassword = passwordSalt + signup.getPassword();
		String passwordHash = DigestUtils.sha256Hex(saltedPassword.getBytes());
		
		User user = new User(signup.getEmail(), passwordHash, passwordSalt);
		user.setDateCreated(System.currentTimeMillis());
		Long userId = userDao.save(user).getId();
		
		return Response.status(Status.CREATED).entity(new MapBuilder().entry("id", userId).build()).build();
	}
	
	@POST
	@Path("reset_pwd")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetPassword(String email) {
		List<User> users = userDao.findBy("email", email);
		if (users.isEmpty()) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND)
					.entity("An account with this email address does not exist").build());
		}
		
		return Response.ok().build();
	}
	
}