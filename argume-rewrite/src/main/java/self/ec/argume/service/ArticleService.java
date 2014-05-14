package self.ec.argume.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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

public class ArticleService {

	private ArticleService() {}

	private static final int DEFAULT_LIMIT = 5;
	private static final long DAY_MILLIS = 24 * 60 * 60 * 1000;
	
	private static final GenericDao<Article> articleDao = DaoFactory.getArticleDao();
	private static final GenericDao<Vote> voteDao = DaoFactory.getVoteDao();
	private static final GenericDao<Like> likeDao = DaoFactory.getLikeDao();
	private static final GenericDao<Argument> argumentDao = DaoFactory.getArgumentDao();
	private static final GenericDao<User> userDao = DaoFactory.getUserDao();
	
	public static Article create(Article article) {
		article.setId(null);
		return saveArticle(article, true);
	}
	
	public static Article update(long id, Article article) {
		Article existing = articleDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}

		article.setId(id);
		article.setDateCreated(existing.getDateCreated());
		return saveArticle(article, false);
	}
	
	private static Article saveArticle(Article article, boolean isNew) {
		if (isNew) {
			article.setDateCreated(System.currentTimeMillis());
		} else {
			article.setDateModified(System.currentTimeMillis());
		}
		return articleDao.save(article);
	}
	
	public static ResultList<Article> list(int page, int pageSize, String search, 
										   Boolean verified, boolean addVoteCounts) {
		ResultList<Article> result = null;
		Criteria criteria = new Criteria().setPagination(page, pageSize).setOrderBy("dateCreated desc");
		if (verified != null) {
			criteria = criteria.addColumn("verified", verified);
		}
		if ("today".equals(search)) {
			criteria = criteria.addColumn("dateCreated", (System.currentTimeMillis() - DAY_MILLIS), Operator.GTE);
		} 
		result = articleDao.query(criteria);
		
		List<Article> articles = result.getObjects();
		if (articles != null && addVoteCounts) {
			for (Article article : articles) {
				addVoteCountsToArticle(article);
			}
		}
		return result;
	}
	
	public static Article getById(long id, boolean includeArguments) {
		Article article = articleDao.findById(id);
		if (article != null) {
			addVoteCountsToArticle(article);
			if (includeArguments) {
				article.setArgumentsFor(getArgumentsForArticle(article, true, true, DEFAULT_LIMIT));
				article.setArgumentsAgainst(getArgumentsForArticle(article, false, true, DEFAULT_LIMIT));
			}
		}
		
		return article;
	}
	
	public static ResultList<Argument> getArgumentsForArticle(Article article, boolean affirmative, 
													  		 boolean verified, int limit) {
		Criteria criteria = new Criteria().addColumn("articleId", article.getId())
										  .addColumn("verified", verified)
										  .addColumn("affirmative", new Boolean(affirmative));
		
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
				arg.setSubmitterImgUrl(argUser.getImgUrl());
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
	
	private static void addVoteCountsToArticle(Article article) {
		int votesFor = (int)voteDao
				.count(new Criteria().addColumn("articleId", article.getId())
				.addColumn("favorable", true));
		article.setVotesFor(votesFor);
		
		int votesAgainst = (int)voteDao.count(new Criteria()
				.addColumn("articleId", article.getId())
				.addColumn("favorable", false));
		article.setVotesAgainst(votesAgainst);
		
		int total = votesFor + votesAgainst;
		if (total > 0) {
			article.setVotesForPct(Math.round(votesFor / (float)total * 100));
			article.setVotesAgainstPct(Math.round(votesAgainst / (float)total * 100));
		}
	}
}
