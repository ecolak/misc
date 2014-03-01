package self.ec.btcbots.resource;

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

import self.ec.btcbots.entity.User;
import self.ec.btcbots.model.Login;
import self.ec.btcbots.util.AuthUtils;

@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

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
	public Response login(final Login login) {
		User authUser = AuthUtils.getAuthenticatedUser(login.getEmail(), login.getPassword());
		if (authUser == null) {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);			
		} 

		authUser.clearPassword();
		request.getSession().setAttribute("user", authUser);
		return Response.status(Status.OK).entity(authUser).build();
	}
}