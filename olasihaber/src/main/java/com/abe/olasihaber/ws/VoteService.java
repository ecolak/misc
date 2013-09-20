package com.abe.olasihaber.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.abe.olasihaber.dao.GenericDao;
import com.abe.olasihaber.model.Result;
import com.abe.olasihaber.model.Vote;

@Path("/vote")
@Produces(MediaType.APPLICATION_JSON)
public class VoteService {

private static final GenericDao<Vote> voteDao = new GenericDao<Vote>(Vote.class);
	
	@Context
	UriInfo uriInfo;

	@Context
	HttpServletRequest request;
	
	@GET
	public Result getAll() {
		return new Result(voteDao.findAll(), 1, 1);
	}

	@GET
	@Path("{id}")
	public Vote getById(@PathParam("id") long id) {
		return voteDao.findById(id);
	}
	
	@GET
	@Path("article/{articleId}/visitor/{visitorId}")
	public Vote getByArticleAndVisitor(@PathParam("articleId") long articleId, @PathParam("visitorId") String visitorId) {
		return getByArticleIdAndVisitorId(articleId, visitorId);
	}
	
	private Vote getByArticleIdAndVisitorId(long articleId, String visitorId) {
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("visitorId", visitorId);
		args.put("articleId", articleId);
		List<Vote> list = voteDao.findBy(args);
		
		return list.isEmpty() ? null : list.get(0);
	}
	
	@POST
	public Response createVote(final Vote vote) {
		Vote existing = getByArticleIdAndVisitorId(vote.getArticleId(), vote.getVisitorId());
		if (existing != null) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(String.format("Visitor id %s has already voted for article %s. Send a PUT request to modify", 
							vote.getVisitorId(), vote.getArticleId())).build());
		}
		return saveVote(vote);
	}
	
	@PUT
	@Path("{id}")
	public Response updateVote(@PathParam("id") long id, final Vote vote) {
		Vote existing = voteDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("Vote does not exist").build());
		}
		
		if (vote.getId() == null) vote.setId(id);
		return saveVote(vote);
	}
	
	private Response saveVote(final Vote vote) {
		vote.setIp(request.getRemoteAddr());
		voteDao.save(vote);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteVote(@PathParam("id") long id) {
		voteDao.deleteBy("id", id);
		return Response.ok().build();
	}
		
}
