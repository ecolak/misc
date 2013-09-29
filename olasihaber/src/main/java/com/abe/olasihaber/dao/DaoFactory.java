package com.abe.olasihaber.dao;

import com.abe.olasihaber.model.Argument;
import com.abe.olasihaber.model.Article;
import com.abe.olasihaber.model.Like;
import com.abe.olasihaber.model.User;
import com.abe.olasihaber.model.UserCredentials;
import com.abe.olasihaber.model.Vote;

public class DaoFactory {

	private DaoFactory() {}
	
	private static final GenericDao<Argument> argumentDao = new GenericDao<Argument>(Argument.class);
	private static final GenericDao<Like> likeDao = new GenericDao<Like>(Like.class);
	private static final GenericDao<Article> articleDao = new GenericDao<Article>(Article.class);
	private static final GenericDao<User> userDao = new GenericDao<User>(User.class);
	private static final GenericDao<UserCredentials> userCredDao = new GenericDao<UserCredentials>(UserCredentials.class);
	private static final GenericDao<Vote> voteDao = new GenericDao<Vote>(Vote.class);
	
	public static GenericDao<Argument> getArgumentDao() {
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
	public static GenericDao<UserCredentials> getUserCredentialsDao() {
		return userCredDao;
	}
	public static GenericDao<Vote> getVoteDao() {
		return voteDao;
	}
	
}
