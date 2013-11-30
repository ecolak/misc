package self.ec.argume.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import self.ec.argume.dao.Criteria;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.ResultList;
import self.ec.argume.model.Vote;
import self.ec.argume.util.Constants;

@Path("/vote")
@Produces(MediaType.APPLICATION_JSON)
public class VoteResource {

	private static final GenericDao<Vote> voteDao = DaoFactory.getVoteDao();

	@Context
	HttpServletRequest request;
	
	@GET
	public ResultList<Vote> getAll(@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page, 
			   					   @DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize) {
		return voteDao.query(new Criteria().setPagination(page, pageSize));
	}

	@GET
	@Path("{id}")
	public Vote getById(@PathParam("id") long id) {
		return voteDao.findById(id);
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteVote(@PathParam("id") long id) {
		voteDao.deleteBy("id", id);
		return Response.ok().build();
	}
		
}