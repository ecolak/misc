package self.ec.argume.resource;

import java.util.Map;

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

import self.ec.argume.model.Article;
import self.ec.argume.model.HtmlResponse;
import self.ec.argume.model.ResultList;
import self.ec.argume.service.ArticleService;
import self.ec.argume.util.Constants;
import self.ec.argume.util.ContentUtils;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class NewsResource {

	private static final String NO_HTML_TAGS_REGEX = "<\\/?[^>]+(>|$)";
	
	@Context
	private HttpServletRequest request;
	
	@GET
    public Viewable index(@DefaultValue(Constants.DEFAULT_PAGE) 
    					  @QueryParam("page") int page,
    					  @DefaultValue(Constants.DEFAULT_PAGE_SIZE) 
    					  @QueryParam("pagesize") int pageSize,
    					  @QueryParam("search") String search) 
	{
		ResultList<Article> result = ArticleService.list(page, pageSize, search, true, true);
        return new Viewable("/news", new HtmlResponse(result, request));
    }
	
	@GET
	@Path("news")
    public Viewable news(@DefaultValue(Constants.DEFAULT_PAGE) 
    					  @QueryParam("page") int page,
    					  @DefaultValue(Constants.DEFAULT_PAGE_SIZE) 
    					  @QueryParam("pagesize") int pageSize,
    					  @QueryParam("search") String search) 
	{
		return index(page, pageSize, search);
    }
	
	@GET
	@Path("news/{id}")
    public Viewable getById(@PathParam("id") int id) 
	{
		Article article = ArticleService.getById(id, true);
		if (article == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}
		Map<String,String> meta = ContentUtils.getDefaultMeta(request);
		meta.put("title", article.getTitle());
		meta.put("image", article.getImgUrl());
		String description = article.getBody();
		String metaDescription = description;
		if (metaDescription != null) {
			metaDescription = metaDescription.replaceAll(NO_HTML_TAGS_REGEX, "");
			if (metaDescription.length() > 200) {
				metaDescription = metaDescription.substring(0, 197) + "...";
			}
		}
		
		meta.put("description", (metaDescription != null ? metaDescription : ""));	
		meta.put("url", request.getRequestURL().toString());
		
        return new Viewable("/article", new HtmlResponse(meta, article, request));
    }
}
