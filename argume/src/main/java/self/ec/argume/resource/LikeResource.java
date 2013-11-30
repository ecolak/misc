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
import self.ec.argume.model.Like;
import self.ec.argume.model.ResultList;
import self.ec.argume.util.Constants;

@Path("/like")
@Produces(MediaType.APPLICATION_JSON)
public class LikeResource {

	private static final GenericDao<Like> likeDao = DaoFactory.getLikeDao();

	@Context
	HttpServletRequest request;
	
	@GET
	public ResultList<Like> getAll(@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page, 
			   					   @DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize) {
		return likeDao.query(new Criteria().setPagination(page, pageSize));
	}

	@GET
	@Path("{id}")
	public Like getById(@PathParam("id") long id) {
		return likeDao.findById(id);
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteLike(@PathParam("id") long id) {
		likeDao.deleteBy("id", id);
		return Response.ok().build();
	}
		
}