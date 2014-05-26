package self.ec.argume.resource.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import self.ec.argume.model.Argument;
import self.ec.argume.service.ArgumentService;

@Path("/api/admin/arguments")
@Produces(MediaType.APPLICATION_JSON)
public class AdminArgumentResource {

	
	@Context
	HttpServletRequest request;
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateArgument(@PathParam("id") long id, final Argument argument) {
		Argument result = ArgumentService.updateArgument(id, argument);
		return Response.ok(result).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteArgument(@PathParam("id") long id) {
		ArgumentService.deleteArgument(id);
		return Response.ok().build();
	}
	
}