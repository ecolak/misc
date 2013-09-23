package com.abe.olasihaber.ws;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.abe.olasihaber.dao.ArgumentDao;
import com.abe.olasihaber.model.Argument;
import com.abe.olasihaber.model.ResultList;
import com.abe.olasihaber.util.Constants;
import com.abe.olasihaber.util.NumberUtils;

@Path("/argument")
@Produces(MediaType.APPLICATION_JSON)
public class ArgumentService {

	private static final int DEFAULT_LIMIT = 5;
	private static final ArgumentDao argumentDao = new ArgumentDao();
	
	@Context
	UriInfo uriInfo;

	@Context
	HttpServletRequest request;
	
	@GET
	public ResultList<Argument> getAll() {
		int page = NumberUtils.toInt(request.getParameter("page"), Constants.DEFAULT_PAGE);
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), Constants.DEFAULT_PAGE_SIZE);
		return argumentDao.listWithTotalPages(page, pageSize);
	}

	@GET
	@Path("{id}")
	public Argument getById(@PathParam("id") long id) {
		return argumentDao.findById(id);
	}
	
	@GET
	@Path("article/{articleId}/type/{type}")
	public ResultList<Argument> getArgumentsByType(@PathParam("articleId") long articleId, @PathParam("type") String type) {
		return argumentDao.getArgumentsByType(articleId, type, NumberUtils.toInt(request.getParameter("limit"), DEFAULT_LIMIT));
	}
	
	@POST
	public Response createArgument(final Argument argument) {
		return saveArgument(argument);
	}
	
	@PUT
	@Path("{id}")
	public Response updateArgument(@PathParam("id") long id, final Argument argument) {
		Argument existing = argumentDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("Argument does not exist").build());
		}
		
		if (argument.getId() == null) argument.setId(id);
		return saveArgument(argument);
	}
	
	private Response saveArgument(final Argument argument) {
		argumentDao.save(argument);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteArgument(@PathParam("id") long id) {
		argumentDao.deleteBy("id", id);
		return Response.ok().build();
	}

}
