package self.ec.argume.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.math.NumberUtils;

import self.ec.argume.dao.Criteria;
import self.ec.argume.dao.Criteria.Operator;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.Argument;
import self.ec.argume.model.Article;
import self.ec.argume.model.Like;
import self.ec.argume.model.ResultList;
import self.ec.argume.model.User;
import self.ec.argume.model.Vote;
import self.ec.argume.model.VoteCounts;
import self.ec.argume.util.AuthUtils;
import self.ec.argume.util.Constants;

@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

	private static final int DEFAULT_LIMIT = 5;
	private static final long DAY_MILLIS = 24*60*60*1000;
	private static final GenericDao<Argument> argumentDao = DaoFactory.getArgumentDao();
	private static final GenericDao<Like> likeDao = DaoFactory.getLikeDao();
	private static final GenericDao<Article> articleDao = DaoFactory.getArticleDao();
	private static final GenericDao<Vote> voteDao = DaoFactory.getVoteDao();
	private static final GenericDao<User> userDao = DaoFactory.getUserDao();

	@Context
	HttpServletRequest request;

	@GET
	public ResultList<Article> list(@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page, 
			   						@DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize) {
		String search = request.getParameter("search");
		if ("today".equals(search)) {
			return articleDao.query(new Criteria().addColumn("dateCreated", 
					(System.currentTimeMillis() - DAY_MILLIS), Operator.GTE)
					.addColumn("verified", true)
					.setOrderBy("dateCreated desc"));
		} else {
			return articleDao.query(new Criteria().setPagination(page, pageSize)
					.addColumn("verified", true)
					.setOrderBy("dateCreated desc"));
		}
	}

	@GET
	@Path("{id}")
	public Article getById(@PathParam("id") long id) {
		Article article = articleDao.findById(id);
		if (article == null || !article.isVerified()) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}
		return article;
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
	public ResultList<Argument> getArgumentsByType(@PathParam("id") long articleId, @QueryParam("type") String type) {
		Criteria criteria = new Criteria().addColumn("articleId", articleId).addColumn("status", Argument.Status.APPROVED);
		Boolean aff = null;
		if ("supporting".equals(type)) {
			aff = true;
		} else if ("opposed".equals(type)) {
			aff = false;
		}
		if (aff != null) criteria.addColumn("affirmative", aff);
		
		// Cannot select by limit because we have to sort by score first
		// TODO: This might become a bottleneck when an article has too many arguments (> 50)
		//		 Make some reasonable assumption for limit here
		List<Argument> arguments = argumentDao.query(criteria).getObjects();
		
		for (Argument arg : arguments) {
			arg.setLikes((int)likeDao.count(new Criteria().addColumn("argumentId", arg.getId()).addColumn("favorable", true)));
			arg.setDislikes((int)likeDao.count(new Criteria().addColumn("argumentId", arg.getId()).addColumn("favorable", false)));
			User argUser = arg.getUserId() != null ? userDao.findById(arg.getUserId()) : null;	
			if (argUser != null) {
				String submittedBy = null;
				arg.setSource(argUser.getSource());
				if (argUser.getFirstName() != null) {
					submittedBy = argUser.getFirstName();
					if (argUser.getLastName() != null) {
						submittedBy += " " + argUser.getLastName();
					}
				}
				arg.setSubmittedBy(submittedBy);
				arg.setUserIdAtSource(argUser.getIdAtSource());
			}
		}
		
		Collections.sort(arguments, new Comparator<Argument>() {
			@Override
			public int compare(Argument arg1, Argument arg2) {
				return -(new Float(Argument.magicScore(arg1.getLikes(), arg1.getDislikes()))
						.compareTo(new Float(Argument.magicScore(arg2.getLikes(), arg2.getDislikes()))));
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
		
		// Let admin manipulate vote counts
		User userInSession = AuthUtils.getUserInSession(request);
		if (userInSession != null && userInSession.isAdmin()) {
			vote.setId(null);
			vote.setUserId(userInSession.getId());
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
		
		vote.setUserId(userInSession != null ? userInSession.getId() : null);
		vote.setVisitorId(AuthUtils.getVisitorIdFromCookies(request));
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