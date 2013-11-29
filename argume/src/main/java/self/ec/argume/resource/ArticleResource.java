package self.ec.argume.resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.lang.time.DateUtils;

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.dao.GenericDao.Operand;
import self.ec.argume.model.Argument;
import self.ec.argume.model.Article;
import self.ec.argume.model.Like;
import self.ec.argume.model.ResultList;
import self.ec.argume.model.User;
import self.ec.argume.model.Vote;
import self.ec.argume.model.VoteCounts;
import self.ec.argume.util.AuthUtils;
import self.ec.argume.util.Constants;
import self.ec.argume.util.NumberUtils;

@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

	private static final int DEFAULT_LIMIT = 5;
	private static final GenericDao<Argument> argumentDao = DaoFactory.getArgumentDao();
	private static final GenericDao<Like> likeDao = DaoFactory.getLikeDao();
	private static final GenericDao<Article> articleDao = DaoFactory.getArticleDao();
	private static final GenericDao<Vote> voteDao = DaoFactory.getVoteDao();

	@Context
	HttpServletRequest request;

	@GET
	public ResultList<Article> list(@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page, 
			   						@DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize) {
		String search = request.getParameter("search");
		if ("today".equals(search)) {
			return articleDao.findByColumnWithTotalPages("pubDate", DateUtils.truncate(new Date(), Calendar.DATE), Operand.GTE); 
		} else {
			return articleDao.listWithTotalPages(page, pageSize, "order by pubDate desc"); 
		}
	}

	@GET
	@Path("{id}")
	public Article getById(@PathParam("id") long id) {
		Article article = articleDao.findById(id);
		if (article == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}
		return article;
	}

	@POST
	public Response createArticle(final Article article) {
		return saveArticle(article, true);
	}

	@PUT
	@Path("{id}")
	public Response updateArticle(@PathParam("id") long id, final Article article) {
		Article existing = articleDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}

		article.setId(id);
		return saveArticle(article, false);
	}

	private Response saveArticle(final Article article, boolean isNew) {
		article.setPubDate(new Date());
		if (isNew) {
			article.setDateCreated(System.currentTimeMillis());
		} else {
			article.setDateModified(System.currentTimeMillis());
		}
		Article result = articleDao.save(article);
		return Response.ok(result).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteArticle(@PathParam("id") long id) {
		articleDao.deleteBy("id", id);
		return Response.ok().build();
	}
	
	@GET
	@Path("{id}/arguments")
	public ResultList<Argument> getArgumentsByType(@PathParam("id") long articleId, @QueryParam("type") String type) {
		Map<String,Object> columnMap = new HashMap<String,Object>();
		columnMap.put("articleId", articleId);
		Boolean aff = null;
		if ("supporting".equals(type)) {
			aff = true;
		} else if ("opposed".equals(type)) {
			aff = false;
		}
		if (aff != null) columnMap.put("affirmative", aff);
		
		columnMap.put("status", Argument.Status.APPROVED);
		
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
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("articleId", articleId);
		args.put("favorable", true);
		int favorableCount = (int)voteDao.count(args);
		
		args = new HashMap<String,Object>();
		args.put("articleId", articleId);
		args.put("favorable", false);
		int againstCount = (int)voteDao.count(args);
		
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
		
		vote.setIp(request.getRemoteAddr());
		User userInSession = AuthUtils.getUserInSession(request);
		vote.setUserId(userInSession != null ? userInSession.getId() : null);
		vote.setVisitorId(AuthUtils.getVisitorIdFromCookies(request));
		Vote result = voteDao.save(vote);
		
		return Response.status(responseStatus).entity(result).build();
	}
	
	private Vote getVoteForArticleAndVisitor(long articleId) {
		Vote result = null;
		
		Map<String,Object> args = new HashMap<String,Object>();
		User userInSession = AuthUtils.getUserInSession(request);
		String visitorId = AuthUtils.getVisitorIdFromCookies(request);
		if (userInSession != null) {
			args.put("userId", userInSession.getId()); 
		} else if (visitorId != null) {
			args.put("visitorId", visitorId); 
		}
		
		args.put("articleId", articleId);
		List<Vote> list = voteDao.findByColumns(args);
		result = list.isEmpty() ? null : list.get(0);
		
		return result;
	}
}