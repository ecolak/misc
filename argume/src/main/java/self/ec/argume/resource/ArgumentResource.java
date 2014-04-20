package self.ec.argume.resource;

import java.io.IOException;
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import self.ec.argume.dao.Criteria;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.Argument;
import self.ec.argume.model.Like;
import self.ec.argume.model.Login;
import self.ec.argume.model.ResultList;
import self.ec.argume.model.User;
import self.ec.argume.util.AuthUtils;
import self.ec.argume.util.Constants;
import self.ec.argume.util.JerseyUtils;
import self.ec.argume.util.Messages;

@Path("/arguments")
@Produces(MediaType.APPLICATION_JSON)
public class ArgumentResource {

	private static final int DEFAULT_MAX_ARG_COUNT = 50;
	private static final GenericDao<Argument> argumentDao = DaoFactory.getArgumentDao();
	private static final GenericDao<Like> likeDao = DaoFactory.getLikeDao();
	private static final Client jerseyClient = ClientBuilder.newClient();
	
	private static final ObjectMapper om = new ObjectMapper();
	
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
		argument.setLoginType(existing.getLoginType());
		
		return saveArgument(argument, false);
	}
	
	private String getFbUserName(Long userId, String accessToken) throws IOException {	
		String result = null;
		WebTarget fbAccessTokenTarget = jerseyClient.target(Constants.FB_GRAPH_API_BASE).path("me");
		Response response = fbAccessTokenTarget.queryParam("access_token", 
							accessToken).request().get();
		if (Status.OK.getStatusCode() == response.getStatus()) {
			JsonNode root = om.readTree(JerseyUtils.getEntityFromResponse(response));
			if (root != null && root.get("name") != null) {
				result = root.get("name").getTextValue();
			}
		}
		
		return result;
	}
	
	private Response saveArgument(final Argument argument, boolean isNew) {
		if (isNew) {
			Long userId = null;
			Login.Type loginType = null;
			String submittedBy = null;
			
			try {
				if (Login.Type.FACEBOOK == argument.getLoginType() && 
					argument.getUserId() != null) {
					String fbUserName = getFbUserName(userId, argument.getFbAccessToken());
					if (fbUserName != null) {
						userId = argument.getUserId();
						loginType = Login.Type.FACEBOOK; 
						submittedBy = fbUserName;
					}
				} else {
					User userInSession = AuthUtils.getUserInSession(request);
					if (userInSession != null) {
						userId = userInSession.getId();
						loginType = Login.Type.ARGUME;
						if (userInSession.getFirstName() != null) {
							submittedBy = userInSession.getFirstName();
							if (userInSession.getLastName() != null) {
								submittedBy += " " + userInSession.getLastName();
							}
						}
					}
				} 
			} catch (IOException ioe) {
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}
			
			argument.setUserId(userId);
			argument.setLoginType(loginType);
			argument.setSubmittedBy(submittedBy);
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