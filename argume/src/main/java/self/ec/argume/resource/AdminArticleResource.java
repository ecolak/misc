package self.ec.argume.resource;

import javax.servlet.http.HttpServletRequest;
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
import self.ec.argume.model.Article;
import self.ec.argume.model.ResultList;
import self.ec.argume.util.Constants;

@Path("/admin/articles")
@Produces(MediaType.APPLICATION_JSON)
public class AdminArticleResource {

	private static final GenericDao<Article> articleDao = DaoFactory
			.getArticleDao();

	@Context
	HttpServletRequest request;

	@GET
	public ResultList<Article> list(
			@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page,
			@DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize) {

		return articleDao.query(new Criteria().setPagination(page, pageSize)
				.setOrderBy("dateCreated desc"));

	}

	@GET
	@Path("{id}")
	public Article getById(@PathParam("id") long id) {
		Article article = articleDao.findById(id);
		if (article == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND)
					.build());
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
		article.setDateCreated(existing.getDateCreated());
		return saveArticle(article, false);
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

	@DELETE
	@Path("{id}")
	public Response deleteArticle(@PathParam("id") long id) {
		articleDao.deleteBy("id", id);
		return Response.ok().build();
	}
}
