package self.ec.argume.resource.api;

import javax.persistence.EntityNotFoundException;
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

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.Article;
import self.ec.argume.model.ResultList;
import self.ec.argume.service.ArticleService;
import self.ec.argume.util.Constants;

@Path("/api/admin/articles")
@Produces(MediaType.APPLICATION_JSON)
public class AdminArticleResource {

	private static final GenericDao<Article> articleDao = DaoFactory.getArticleDao();
	
	@Context
	HttpServletRequest request;

	@GET
	public ResultList<Article> list(@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page,
									@DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize,
									@QueryParam("verified") Boolean verified) {
		return ArticleService.list(page, pageSize, null, verified, false);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createArticle(final Article article) {
		return Response.ok(ArticleService.create(article)).build();
	}
	
	@PUT
	@Path("{id}")
	public Response updateArticle(@PathParam("id") long id, final Article article) {
		Article result = null;
		try {
			result = ArticleService.update(id, article); 
		} catch (EntityNotFoundException e) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}
		
		return Response.ok().entity(result).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteArticle(@PathParam("id") long id) {
		articleDao.deleteBy("id", id);
		return Response.ok().build();
	}
}
