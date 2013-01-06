package emre.recossandra.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import emre.recossandra.dao.ProductDao;
import emre.recossandra.model.Product;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductService {

	@Context
	UriInfo uriInfo;
	
	@Context
	Request request;

	@GET
	public List<Product> getAll() {
		List<Product> result = new ArrayList<Product>();
		try { 
			result = ProductDao.getAll(); 
		} catch(Exception e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return result;
	}
}
