package com.abe.olasihaber.ws;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abe.olasihaber.dao.GenericDao;
import com.abe.olasihaber.model.Article;
import com.abe.olasihaber.model.Result;

@Path("/article")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleService {

	private static final Logger LOG = LoggerFactory.getLogger(ArticleService.class);
	
	private static final GenericDao<Article> articleDao = new GenericDao<Article>(Article.class);
	
	@Context
	UriInfo uriInfo;

	@Context
	HttpServletRequest request;
	
	@GET
	public Result getAll() {
		return new Result(articleDao.findAll(), 1, 1);
	}

	@GET
	@Path("{id}")
	public Article getById(@PathParam("id") long id) {
		return articleDao.findById(id);
	}
	
	@POST
	public Response createArticle(final Article article) {
		return saveArticle(article);
	}
	
	@PUT
	@Path("{id}")
	public Response updateArticle(@PathParam("id") long id, final Article article) {
		Article existing = articleDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("Article does not exist").build());
		}
		
		if (article.getId() == null) article.setId(id);
		return saveArticle(article);
	}
	
	private Response saveArticle(final Article article) {
		articleDao.save(article);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteArticle(@PathParam("id") long id) {
		articleDao.deleteBy("id", id);
		return Response.ok().build();
	}

}