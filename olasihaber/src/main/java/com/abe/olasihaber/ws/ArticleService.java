package com.abe.olasihaber.ws;

import java.util.Calendar;
import java.util.Date;

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

import org.apache.commons.lang.time.DateUtils;

import com.abe.olasihaber.dao.DaoFactory;
import com.abe.olasihaber.dao.GenericDao;
import com.abe.olasihaber.dao.GenericDao.Operand;
import com.abe.olasihaber.model.Article;
import com.abe.olasihaber.model.ResultList;
import com.abe.olasihaber.util.AuthUtils;
import com.abe.olasihaber.util.Constants;
import com.abe.olasihaber.util.NumberUtils;

@Path("/article")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleService {

	private static final GenericDao<Article> articleDao = DaoFactory.getArticleDao();

	@Context
	UriInfo uriInfo;

	@Context
	HttpServletRequest request;

	@GET
	public ResultList<Article> list() {
		int page = NumberUtils.toInt(request.getParameter("page"), Constants.DEFAULT_PAGE);
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), Constants.DEFAULT_PAGE_SIZE);
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
		return articleDao.findById(id);
	}

	@POST
	public Response createArticle(final Article article) {
		if (!AuthUtils.isUserInSessionAdmin(request)) {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		
		return saveArticle(article);
	}

	@PUT
	@Path("{id}")
	public Response updateArticle(@PathParam("id") long id, final Article article) {
		if (!AuthUtils.isUserInSessionAdmin(request)) {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		
		Article existing = articleDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND)
					.entity("Article does not exist").build());
		}

		if (article.getId() == null)
			article.setId(id);
		return saveArticle(article);
	}

	private Response saveArticle(final Article article) {
		article.setPubDate(new Date());
		Article result = articleDao.save(article);
		return Response.ok(result).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteArticle(@PathParam("id") long id) {
		articleDao.deleteBy("id", id);
		return Response.ok().build();
	}

}
