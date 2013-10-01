package com.abe.olasihaber.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.abe.olasihaber.dao.DaoFactory;
import com.abe.olasihaber.dao.GenericDao;
import com.abe.olasihaber.model.Argument;
import com.abe.olasihaber.model.Like;
import com.abe.olasihaber.model.ResultList;
import com.abe.olasihaber.util.AuthUtils;
import com.abe.olasihaber.util.Constants;
import com.abe.olasihaber.util.NumberUtils;

@Path("/argument")
@Produces(MediaType.APPLICATION_JSON)
public class ArgumentService {

	private static final int DEFAULT_LIMIT = 5;
	private static final int DEFAULT_MAX_ARG_COUNT = 10;
	private static final GenericDao<Argument> argumentDao = DaoFactory.getArgumentDao();
	private static final GenericDao<Like> likeDao = DaoFactory.getLikeDao();
	
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
		Map<String,Object> columnMap = new HashMap<String,Object>();
		columnMap.put("articleId", articleId);
		Boolean aff = null;
		if ("supporting".equals(type)) {
			aff = true;
		} else if ("opposed".equals(type)) {
			aff = false;
		}
		if (aff != null) columnMap.put("affirmative", aff);
		
		// Cannot select by limit because we have to sort by score first
		// TODO: This might become a bottleneck when an article has too many arguments (> 50)
		//		 Make some reasonable assumption for limit here
		List<Argument> arguments = argumentDao.findByColumns(columnMap);
		
		for (Argument arg : arguments) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("argumentId", arg.getId());
			map.put("favorable", true);
			arg.setLikes((int)likeDao.count(map));
			
			map = new HashMap<String,Object>();
			map.put("argumentId", arg.getId());
			map.put("favorable", false);
			arg.setDislikes((int)likeDao.count(map));
		}
		
		Collections.sort(arguments, new Comparator<Argument>() {
			private static final float MAGIC_NUMBER_LAMBDA = 100f;
			private static final float MU = 0.1f;
			
			private float getScore(Argument arg) {
				return (arg.getLikes() - arg.getDislikes()) + (2 * arg.getLikes()  / MAGIC_NUMBER_LAMBDA) - (arg.getDislikes() * MU);
			}
			
			public int compare(Argument arg1, Argument arg2) {
				return -(new Float(getScore(arg1)).compareTo(new Float(getScore(arg2))));
			}			
		});
		
		int limit = NumberUtils.toInt(request.getParameter("limit"), DEFAULT_LIMIT);
		int totalRows = arguments.size();
		if (arguments.size() > limit) {
			List<Argument> trimmed = new ArrayList<Argument>(limit);
			for (int i = 0; i < limit; i++) {
				trimmed.add(arguments.get(i));
			}
			arguments = trimmed;
		}
	
		return new ResultList<Argument>(arguments, 1, limit, totalRows);
	}
	
	@POST
	public Response createArgument(final Argument argument) {
		Map<String,Object> colMap = new HashMap<String,Object>();
		colMap.put("articleId", argument.getArticleId());
		colMap.put("affirmative", argument.isAffirmative());
		colMap.put("status", Argument.Status.approved);
		
		int count = (int)argumentDao.count(colMap);
		if (count >= DEFAULT_MAX_ARG_COUNT) {
			throw new WebApplicationException(Response.status(Status.FORBIDDEN)
						.entity("Max number of arguments for this article and side have already been reached")
						.build());
		}
		
		return saveArgument(argument);
	}
	
	@PUT
	@Path("{id}")
	public Response updateArgument(@PathParam("id") long id, final Argument argument) {
		if (!AuthUtils.isUserInSessionAdmin(request)) {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		
		Argument existing = argumentDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("Argument does not exist").build());
		}
		
		if (argument.getId() == null) argument.setId(id);
		return saveArgument(argument);
	}
	
	private Response saveArgument(final Argument argument) {
		Argument result = argumentDao.save(argument);
		return Response.ok(result).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteArgument(@PathParam("id") long id) {
		argumentDao.deleteBy("id", id);
		return Response.ok().build();
	}

}
