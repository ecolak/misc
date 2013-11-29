package self.ec.argume.dao;

import self.ec.argume.model.Article;
import self.ec.argume.model.Like;
import self.ec.argume.model.User;
import self.ec.argume.model.Vote;

public class DaoFactory {

	private DaoFactory() {}
	
	private static final ArgumentDao argumentDao = new ArgumentDao();
	private static final GenericDao<Like> likeDao = new GenericDao<Like>(Like.class);
	private static final GenericDao<Article> articleDao = new GenericDao<Article>(Article.class);
	private static final GenericDao<User> userDao = new GenericDao<User>(User.class);
	private static final GenericDao<Vote> voteDao = new GenericDao<Vote>(Vote.class);
	
	public static ArgumentDao getArgumentDao() {
		return argumentDao;
	}
	public static GenericDao<Like> getLikeDao() {
		return likeDao;
	}
	public static GenericDao<Article> getArticleDao() {
		return articleDao;
	}
	public static GenericDao<User> getUserDao() {
		return userDao;
	}
	public static GenericDao<Vote> getVoteDao() {
		return voteDao;
	}
	
}
