package self.ec.argume.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import self.ec.argume.dao.ArgumentDao;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.model.Argument;
import self.ec.argume.model.Dashboard;
import self.ec.argume.util.AuthUtils;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

	public static final int DEFAULT_LIMIT = 20;
	private static final ArgumentDao argumentDao = DaoFactory.getArgumentDao();
	
	@Context
	HttpServletRequest request;
	
	@GET
	public Dashboard getDashboard() {
		int totalLikes = 0;
		int totalDislikes = 0;
		List<Argument> arguments = argumentDao.getArgumentsForUser(AuthUtils.getUserInSession(request).getId(), DEFAULT_LIMIT).getObjects();
		
		for (Argument arg : arguments) {
			totalLikes += arg.getLikes();
			totalDislikes += arg.getDislikes();
		}
		
		int diff = totalLikes - totalDislikes;
		
		return new Dashboard((diff > 0 ? (int)Math.round(Argument.magicScore(totalLikes, totalDislikes)) : 0), arguments);
	}

}