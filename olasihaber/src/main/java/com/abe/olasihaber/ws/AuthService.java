package com.abe.olasihaber.ws;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.abe.olasihaber.model.Login;
import com.abe.olasihaber.model.User;
import com.abe.olasihaber.util.AuthUtils;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthService {
	
	@Context
	HttpServletRequest request;
	
	@Context
	HttpServletResponse response;
	
	@GET
	@Path("session_user")
	public User getSessionUser() {
		return (User) request.getSession().getAttribute("session-user");
	}
	
	@POST
	@Path("login")
	public Response userLogin(final Login login) {
		User authUser = AuthUtils.getAuthenticatedUser(login.getUsername(), login.getPassword());
		if (authUser == null) {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);			
		} 

		request.getSession().setAttribute("session-user", authUser);
		return Response.ok().build();
	}
}
