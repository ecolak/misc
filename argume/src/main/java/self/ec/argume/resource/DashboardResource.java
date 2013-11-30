package self.ec.argume.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.math.NumberUtils;

import self.ec.argume.dao.ArgumentDao;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.model.Argument;
import self.ec.argume.model.ResultList;
import self.ec.argume.util.AuthUtils;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

	public static final int DEFAULT_LIMIT = 10;
	private static final ArgumentDao argumentDao = DaoFactory.getArgumentDao();
	
	@Context
	HttpServletRequest request;
	
	@GET
	@Path("arguments")
	public ResultList<Argument> getArguments() {
		return argumentDao.getArgumentsForUser(AuthUtils.getUserInSession(request).getId(), NumberUtils.toInt(request.getParameter("limit"), DEFAULT_LIMIT));
	}
	
	@GET
	@Path("argume_score")
	public int getArgumeScore() {
		return argumentDao.getArgumeScoreForUser(AuthUtils.getUserInSession(request).getId());
	}

}