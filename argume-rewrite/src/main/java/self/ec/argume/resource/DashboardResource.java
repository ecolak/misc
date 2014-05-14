package self.ec.argume.resource;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.math.NumberUtils;
import org.glassfish.jersey.server.mvc.Viewable;

import self.ec.argume.dao.ArgumentDao;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.model.Argument;
import self.ec.argume.model.HtmlResponse;
import self.ec.argume.model.ResultList;
import self.ec.argume.util.AuthUtils;

@Path("/dashboard")
public class DashboardResource {

	public static final int DEFAULT_LIMIT = 10;
	private static final ArgumentDao argumentDao = DaoFactory.getArgumentDao();
	
	@Context
	HttpServletRequest request;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getDashboard() {
		ResultList<Argument> arguments = argumentDao.getArgumentsForUser(
				AuthUtils.getUserInSession(request).getId(), 
				NumberUtils.toInt(request.getParameter("limit"), DEFAULT_LIMIT));
		Map<String,Object> map = arguments.toMap();
		map.put("argumeScore", argumentDao.getArgumeScoreForUser(
				AuthUtils.getUserInSession(request).getId()));
		map.put("nextLimit", (arguments.getPage() + 1) * arguments.getPageSize());
		return new Viewable("/dashboard", new HtmlResponse(map, request));
	}
	
}