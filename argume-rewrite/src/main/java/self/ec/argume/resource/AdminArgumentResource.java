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

import self.ec.argume.dao.Criteria;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.Argument;
import self.ec.argume.model.HtmlResponse;
import self.ec.argume.model.ResultList;
import self.ec.argume.util.Constants;

@Path("/admin/arguments")
@Produces(MediaType.TEXT_HTML)
public class AdminArgumentResource {

	private static final GenericDao<Argument> argumentDao = DaoFactory.getArgumentDao();
	
	@Context
	HttpServletRequest request;
	
	@GET
	public Viewable getAll(@DefaultValue(Constants.DEFAULT_PAGE) @QueryParam("page") int page, 
						   @DefaultValue(Constants.DEFAULT_PAGE_SIZE) @QueryParam("pagesize") int pageSize,
						   @QueryParam("verified") Boolean verified) {	
		Criteria c = new Criteria().setOrderBy("dateCreated desc").setPagination(page, pageSize);
		if (verified != null) {
			c.addColumn("verified", verified);
		}
		ResultList<Argument> result = argumentDao.query(c);
		return new Viewable("/admin/arguments", new HtmlResponse(result, request));
	}
	
	@GET
	@Path("{id}")
	public Viewable getById(@PathParam("id") long id) {	
		Argument argument = argumentDao.findById(id);
		if (argument == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}
		
		return new Viewable("/admin/save_argument", 
							new HtmlResponse(argument, request));
	}
}
