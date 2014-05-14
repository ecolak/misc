package self.ec.argume.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.mvc.Viewable;

import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.Article;
import self.ec.argume.model.HtmlResponse;
import self.ec.argume.model.ResultList;
import self.ec.argume.service.ArticleService;
import self.ec.argume.util.Constants;

@Path("/admin/articles")
@Produces(MediaType.TEXT_HTML)
public class AdminArticleResource {

	private static final GenericDao<Article> articleDao = DaoFactory.getArticleDao();

	@Context
	HttpServletRequest request;

	@GET
	public Viewable list(@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page,
						 @DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize,
						 @QueryParam("verified") Boolean verified) {
		ResultList<Article> result = ArticleService.list(page, pageSize, null, verified, false);
		return new Viewable("/admin/articles", new HtmlResponse(result, request));
	}

	@GET
	@Path("{id}")
	public Viewable getById(@PathParam("id") long id) {
		Article article = articleDao.findById(id);
		if (article == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND)
					.build());
		}
		return new Viewable("/admin/save_article", 
							new HtmlResponse(article, request));
	}
	
	@GET
	@Path("new")
	public Viewable newArticle() {
		return new Viewable("/admin/save_article", new HtmlResponse(request));
	}
	
}
