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

import java.util.List;

import com.abe.olasihaber.dao.MockData;
import com.abe.olasihaber.model.Article;
import com.abe.olasihaber.model.Arguments;
import com.abe.olasihaber.model.Vote;

@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleService {

	@Context
	UriInfo uriInfo;

	@Context
	HttpServletRequest request;
	
	@GET
	public List<Article> getAll() {
		return MockData.getAllArticles(); 
	}

	@GET
	@Path("{id}")
	public Article getById(@PathParam("id") long id) {
		return MockData.getArticleById(id);
	}
	
	@GET
	@Path("{id}/arguments")
	public Arguments getArguments(@PathParam("id") long id) {
		return MockData.getArgumentsForArticle(id);
	}
	
	@POST
	@Path("/vote")
	public Response saveVote(final Vote vote) {
		System.out.println("ip " + request.getRemoteAddr() + " voting " + vote.getVote_type() + 
							" article " + vote.getArticle_id());
		MockData.saveVote(new Vote(vote.getArticle_id(), request.getRemoteAddr(), vote.getVote_type()));
		return Response.ok().build();
	}
	
	@GET
	@Path("{id}/vote")
	public Vote getVote(@PathParam("id") long id) {
		return MockData.getVoteByIpAndArticle(request.getRemoteAddr(), id);
	}
	
	@PUT
	@Path("/{id}/vote")
	public Response updateVote(@PathParam("id") long id, final Vote vote) {
		System.out.println("ip " + request.getRemoteAddr() + " voting " + vote.getVote_type() + 
							" article " + vote.getArticle_id());
		MockData.saveVote(new Vote(vote.getArticle_id(), request.getRemoteAddr(), vote.getVote_type()));
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{id}/vote")
	public Response deleteVote(@PathParam("id") long id) {
		System.out.println("ip " + request.getRemoteAddr() + " went undecided about article " +
							" article " + id);
		MockData.deleteVoteByIpAndArticle(request.getRemoteAddr(), id);
		return Response.ok().build();
	}

}
