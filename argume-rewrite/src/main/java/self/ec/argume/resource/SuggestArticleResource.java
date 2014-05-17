package self.ec.argume.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.server.mvc.Viewable;

import self.ec.argume.model.Article;
import self.ec.argume.model.HtmlResponse;
import self.ec.argume.service.ArticleService;
import self.ec.argume.util.MapBuilder;
import self.ec.argume.util.Messages;

@Path("/suggest_article")
@Produces(MediaType.TEXT_HTML)
public class SuggestArticleResource {

	@Context
	HttpServletRequest request;
	
	@GET
    public Viewable index() {
        return new Viewable("/suggest_article", new HtmlResponse(request));
    }
	
	@POST
	public Viewable create(@FormParam("title") String title,
						   @FormParam("img_url") String imgUrl,
						   @FormParam("body") String body,
						   @FormParam("source") String source,
						   @FormParam("location") String location) {
		if (StringUtils.isBlank(title)) {
			return getErrorResponse("errors.suggestArticle.blankTitle");
		}
		if (StringUtils.isBlank(body)) {
			return getErrorResponse("errors.suggestArticle.blankBody");
		}
		if (StringUtils.isBlank(source)) {
			return getErrorResponse("errors.suggestArticle.blankSource");
		}
		
		ArticleService.create(new Article(title, body, imgUrl, null, 0L, source, location, false));
		return getSuccessResponse("success.suggestArticle");
	}
	
	private Viewable getErrorResponse(String messageKey) {
		return getResponse(messageKey, "errorMessage");
	}
	
	private Viewable getSuccessResponse(String messageKey) {
		return getResponse(messageKey, "successMessage");
	}
	
	private Viewable getResponse(String messageKey, String messageType) {
		return new Viewable("/suggest_article", new HtmlResponse(new MapBuilder<String,String>()
				.put(messageType, Messages.getMessage(messageKey))
				.build(), request));
	}
}
