package self.ec.argume.resource.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import self.ec.argume.dao.Criteria;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.Argument;
import self.ec.argume.model.Article;
import self.ec.argume.model.ResultList;
import self.ec.argume.model.User;
import self.ec.argume.model.Vote;
import self.ec.argume.model.VoteCounts;
import self.ec.argume.service.ArticleService;
import self.ec.argume.util.AuthUtils;
import self.ec.argume.util.Constants;

@Path("/api/articles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {
	
	private static final GenericDao<Article> articleDao = DaoFactory.getArticleDao();
	private static final GenericDao<Vote> voteDao = DaoFactory.getVoteDao();

	@Context
	HttpServletRequest request;

	@GET
	public ResultList<Article> list(@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page, 
			   						@DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize,
			   						@QueryParam("search") String search) {	
		return ArticleService.list(page, pageSize, search, true, true);
	}

	@GET
	@Path("{id}")
	public Article getById(@PathParam("id") long id) {
		return ArticleService.getById(id, false);
	}
	
	@POST
	@Path("suggest")
	public Response suggestArticle(final Article article) {
		article.setVerified(false);
		return saveArticle(article, true);
	}
	
	private Response saveArticle(final Article article, boolean isNew) {
		if (isNew) {
			article.setDateCreated(System.currentTimeMillis());
		} else {
			article.setDateModified(System.currentTimeMillis());
		}
		Article result = articleDao.save(article);
		return Response.ok(result).build();
	}
	
	@GET
	@Path("{id}/arguments")
	public ResultList<Argument> getArgumentsByType(@PathParam("id") long articleId, 
												   @QueryParam("type") String type,
												   @DefaultValue(Constants.DEFAULT_ARGS_LIMIT) @QueryParam("limit") int limit) {
		Article article = articleDao.findById(articleId);
		if (article != null) {
			return ArticleService.getArgumentsForArticle(article, "supporting".equals(type), true, limit);
		}
		
		return null;
	}
	
	@GET
	@Path("{id}/vote_counts")
	public VoteCounts getVoteCount(@PathParam("id") long articleId) {
		int favorableCount = (int)voteDao.count(new Criteria().addColumn("articleId", articleId).addColumn("favorable", true));
		int againstCount = (int)voteDao.count(new Criteria().addColumn("articleId", articleId).addColumn("favorable", false));
		
		return new VoteCounts(favorableCount, againstCount);
	}

	@GET
	@Path("{id}/vote")
	public Vote getVoteForArticle(@PathParam("id") long articleId) {
		return getVoteForArticleAndVisitor(articleId);
	}
	
	@DELETE
	@Path("{id}/vote")
	public Response deleteVoteForArticle(@PathParam("id") long articleId) {
		Vote existing = getVoteForArticleAndVisitor(articleId);
		if (existing != null) {
			voteDao.deleteBy("id", existing.getId()); 
		}
		return Response.ok().build();
		
	}
	
	@POST
	@Path("{id}/vote")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createVote(@PathParam("id") long articleId, final Vote vote) {
		vote.setIp(request.getRemoteAddr());
		vote.setArticleId(articleId);	
		vote.setVisitorId(AuthUtils.getVisitorIdFromCookies(request));
		
		// Let admin manipulate vote counts
		User userInSession = AuthUtils.getUserInSession(request);
		vote.setUserId(userInSession != null ? userInSession.getId() : null);	
		
		if (userInSession != null && userInSession.isAdmin()) {
			vote.setId(null);
			vote.setDateCreated(System.currentTimeMillis());
			Vote result = voteDao.save(vote);
			return Response.status(Status.CREATED).entity(result).build();
		}
		
		Vote existing = getVoteForArticleAndVisitor(articleId);
		if (existing != null && existing.isFavorable() == vote.isFavorable()) {
			return Response.status(Status.NOT_MODIFIED).build();
		}
		
		Status responseStatus = null;
		if (existing != null) {
			vote.setId(existing.getId());
			vote.setDateModified(System.currentTimeMillis());
			responseStatus = Status.OK;
		} else {
			vote.setDateCreated(System.currentTimeMillis());
			responseStatus = Status.CREATED;
		}
		
		Vote result = voteDao.save(vote);
		return Response.status(responseStatus).entity(result).build();
	}
	
	private Vote getVoteForArticleAndVisitor(long articleId) {
		Vote result = null;
		
		Criteria criteria = new Criteria().addColumn("articleId", articleId);

		User userInSession = AuthUtils.getUserInSession(request);
		String visitorId = AuthUtils.getVisitorIdFromCookies(request);
		if (userInSession != null) {
			criteria.addColumn("userId", userInSession.getId());
		} else if (visitorId != null) {
			criteria.addColumn("visitorId", visitorId);
		}
		
		List<Vote> list = voteDao.query(criteria).getObjects();
		result = list.isEmpty() ? null : list.get(0);
		
		return result;
	}
}