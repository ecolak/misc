package self.ec.argume.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import self.ec.argume.dao.Criteria;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.Argument;
import self.ec.argume.model.Like;
import self.ec.argume.model.ResultList;
import self.ec.argume.model.User;
import self.ec.argume.util.AuthUtils;
import self.ec.argume.util.Constants;
import self.ec.argume.util.Messages;

@Path("/arguments")
@Produces(MediaType.APPLICATION_JSON)
public class ArgumentResource {

	private static final int DEFAULT_MAX_ARG_COUNT = 50;
	private static final GenericDao<Argument> argumentDao = DaoFactory.getArgumentDao();
	private static final GenericDao<Like> likeDao = DaoFactory.getLikeDao();
	
	@Context
	HttpServletRequest request;
	
	@GET
	public ResultList<Argument> getAll(@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page, 
									   @DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize) {
		return argumentDao.query(new Criteria().setPagination(page, pageSize).setOrderBy("dateCreated desc"));
	}

	@GET
	@Path("{id}")
	public Argument getById(@PathParam("id") long id) {
		Argument argument = argumentDao.findById(id);
		if (argument == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}
		return argument;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createArgument(final Argument argument) {
		int count = (int)argumentDao.count(new Criteria().addColumn("articleId", argument.getArticleId())
								.addColumn("affirmative", argument.isAffirmative())
								.addColumn("status", Argument.Status.APPROVED));
		if (count >= DEFAULT_MAX_ARG_COUNT) {
			throw new WebApplicationException(Response.status(Status.CONFLICT)
						.entity(Messages.getMessage("errors.arguments.maxReached")).build());
		}
		
		return saveArgument(argument, true);
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateArgument(@PathParam("id") long id, final Argument argument) {
		if (!AuthUtils.isUserInSessionAdmin(request)) {
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
		
		Argument existing = argumentDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}
		
		argument.setId(id);	
		argument.setUserId(existing.getUserId());
		
		return saveArgument(argument, false);
	}
	
	private Response saveArgument(final Argument argument, boolean isNew) {
		if (isNew) {
			User userInSession = AuthUtils.getUserInSession(request);
			if (userInSession != null) {
				argument.setUserId(userInSession.getId());
			}
				
			argument.setDateCreated(System.currentTimeMillis());
		} else {
			argument.setDateModified(System.currentTimeMillis());
		}
		
		Argument result = argumentDao.save(argument);
		return Response.ok(result).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteArgument(@PathParam("id") long id) {
		argumentDao.deleteBy("id", id);
		return Response.ok().build();
	}
	
	@GET
	@Path("{id}/like")
	public Like getLikeByArgument(@PathParam("id") long argumentId) {
		return getLikeForArgumentAndVisitor(argumentId);
	}
	
	@POST
	@Path("{id}/like")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createLike(@PathParam("id") long argumentId, final Like like) {
		like.setIp(request.getRemoteAddr());
		
		// Let admin manipulate like counts
		User userInSession = AuthUtils.getUserInSession(request);
		if (userInSession != null && userInSession.isAdmin()) {
			like.setId(null);
			like.setUserId(userInSession.getId());
			like.setDateCreated(System.currentTimeMillis());
			likeDao.save(like);
			return Response.status(Status.CREATED).build();
		}
		
		Like existing = getLikeForArgumentAndVisitor(argumentId);
		if (existing != null && existing.isFavorable() == like.isFavorable()) {
			return Response.status(Status.NOT_MODIFIED).build();
		}
		
		Status responseStatus = null;
		if (existing != null) {
			like.setId(existing.getId());
			like.setDateModified(System.currentTimeMillis());
			responseStatus = Status.OK;
		} else {
			like.setDateCreated(System.currentTimeMillis());
			responseStatus = Status.CREATED;
		}
		
		like.setUserId(userInSession != null ? userInSession.getId() : null);
		like.setVisitorId(AuthUtils.getVisitorIdFromCookies(request));
		
		likeDao.save(like);
		
		return Response.status(responseStatus).build();
	}
	
	private Like getLikeForArgumentAndVisitor(long argumentId) {
		Like result = null;
		
		Criteria criteria = new Criteria().addColumn("argumentId", argumentId);
		
		User userInSession = AuthUtils.getUserInSession(request);
		String visitorId = AuthUtils.getVisitorIdFromCookies(request);
		if (userInSession != null) {
			criteria.addColumn("userId", userInSession.getId());
		} else if (visitorId != null) {
			criteria.addColumn("visitorId", visitorId);
		}
		
		List<Like> list = likeDao.query(criteria).getObjects();
		result = list.isEmpty() ? null : list.get(0);
		
		return result;
	}

}