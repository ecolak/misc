package com.abe.olasihaber.ws;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.abe.olasihaber.dao.MockData;
import com.abe.olasihaber.model.Vote;

@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleService {

	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final String LOCALHOST = "http://localhost:280";
	private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	private static final String ALLOWED_METHODS = "POST, GET, OPTIONS, DELETE, PUT";
	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	private static final String ALLOWED_HEADERS = "X-Requested-With, Content-Type";

	@Context
	UriInfo uriInfo;

	@Context
	HttpServletRequest request;
	
	@GET
	public Response getAll() {
		return makeResponse(MockData.getAllArticles()); 
	}

	@GET
	@Path("{id}")
	public Response getById(@PathParam("id") long id) {
		return makeResponse(MockData.getArticleById(id));
	}
	
	@GET
	@Path("{id}/arguments")
	public Response getArguments(@PathParam("id") long id) {
		return makeResponse(MockData.getArgumentsForArticle(id));
	}
	
	@POST
	@Path("/vote")
	public Response saveVote(final Vote vote) {
		System.out.println("ip " + request.getRemoteAddr() + " voting " + vote.getVote_type() + 
							" article " + vote.getArticle_id());
		MockData.saveVote(new Vote(vote.getArticle_id(), request.getRemoteAddr(), vote.getVote_type()));
		return makeResponse(null);
	}
	
	@GET
	@Path("{id}/vote")
	public Response getVote(@PathParam("id") long id) {
		return makeResponse(MockData.getVoteByIpAndArticle(request.getRemoteAddr(), id));
	}
	
	@PUT
	@Path("/{id}/vote")
	public Response updateVote(@PathParam("id") long id, final Vote vote) {
		System.out.println("ip " + request.getRemoteAddr() + " voting " + vote.getVote_type() + 
							" article " + vote.getArticle_id());
		MockData.saveVote(new Vote(vote.getArticle_id(), request.getRemoteAddr(), vote.getVote_type()));
		return makeResponse(null);
	}
	
	@DELETE
	@Path("/{id}/vote")
	public Response deleteVote(@PathParam("id") long id) {
		System.out.println("ip " + request.getRemoteAddr() + " went undecided about article " +
							" article " + id);
		MockData.deleteVoteByIpAndArticle(request.getRemoteAddr(), id);
		return makeResponse(null);
	}
	
	private Response makeResponse(Object entity) {
		ResponseBuilder rb = entity != null ? Response.ok(entity) : Response.ok();
		return rb
				.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
				.header(ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS)
				.header(ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS).build();
	}
}
