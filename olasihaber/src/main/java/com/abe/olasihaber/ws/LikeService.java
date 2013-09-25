package com.abe.olasihaber.ws;

import java.util.HashMap;
import java.util.Map;

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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.abe.olasihaber.dao.GenericDao;
import com.abe.olasihaber.model.ResultList;
import com.abe.olasihaber.model.Like;
import com.abe.olasihaber.util.Constants;
import com.abe.olasihaber.util.NumberUtils;

@Path("/like")
@Produces(MediaType.APPLICATION_JSON)
public class LikeService {

private static final GenericDao<Like> likeDao = new GenericDao<Like>(Like.class);
	
	@Context
	UriInfo uriInfo;

	@Context
	HttpServletRequest request;
	
	@GET
	public ResultList<Like> getAll() {
		int page = NumberUtils.toInt(request.getParameter("page"), Constants.DEFAULT_PAGE);
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), Constants.DEFAULT_PAGE_SIZE);
		return likeDao.listWithTotalPages(page, pageSize);
	}

	@GET
	@Path("{id}")
	public Like getById(@PathParam("id") long id) {
		return likeDao.findById(id);
	}
	
	@GET
	@Path("{argumentId}/counts")
	public long getLikeCount(@PathParam("argumentId") long argumentId) {
		return likeDao.count("argumentId", argumentId);
	}
	
	@GET
	@Path("argument/{argumentId}/visitor/{visitorId}")
	public Like getByArgumentAndVisitor(@PathParam("argumentId") long argumentId, @PathParam("visitorId") String visitorId) {
		return getByArgumentIdAndVisitorId(argumentId, visitorId);
	}
	
	private Like getByArgumentIdAndVisitorId(long argumentId, String visitorId) {
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("visitorId", visitorId);
		args.put("argumentId", argumentId);
		ResultList<Like> list = likeDao.findByColumnsWithTotalPages(args);
		
		return list.getObjects().isEmpty() ? null : list.getObjects().get(0);
	}
	
	@POST
	public Response createLike(final Like like) {
		Like existing = getByArgumentIdAndVisitorId(like.getArgumentId(), like.getVisitorId());
		if (existing != null) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(String.format("Visitor id %s has already liked argument %d. Send a PUT request to modify", 
							like.getVisitorId(), like.getArgumentId())).build());
		}
		return saveLike(like);
	}
	
	@PUT
	@Path("{id}")
	public Response updateLike(@PathParam("id") long id, final Like like) {
		Like existing = likeDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("Like does not exist").build());
		}
		
		if (like.getId() == null) like.setId(id);
		return saveLike(like);
	}
	
	private Response saveLike(final Like like) {
		like.setIp(request.getRemoteAddr());
		Like result = likeDao.save(like);
		return Response.ok(result).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteLike(@PathParam("id") long id) {
		likeDao.deleteBy("id", id);
		return Response.ok().build();
	}
		
}
