package com.abe.olasihaber.ws;

import java.util.HashMap;
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

import com.abe.olasihaber.dao.DaoFactory;
import com.abe.olasihaber.dao.GenericDao;
import com.abe.olasihaber.model.ResultList;
import com.abe.olasihaber.model.Vote;
import com.abe.olasihaber.util.Constants;
import com.abe.olasihaber.util.NumberUtils;

@Path("/vote")
@Produces(MediaType.APPLICATION_JSON)
public class VoteService {

	private static final GenericDao<Vote> voteDao = DaoFactory.getVoteDao();
	
	private static class VoteCounts {
		private int favorable;
		private int against;
		
		public VoteCounts() {}
		
		public VoteCounts(int favorable, int against) {
			this.favorable = favorable;
			this.against = against;
		}

		public int getFavorable() {
			return favorable;
		}
		public void setFavorable(int favorable) {
			this.favorable = favorable;
		}
		public int getAgainst() {
			return against;
		}
		public void setAgainst(int against) {
			this.against = against;
		}
	}
	
	@Context
	UriInfo uriInfo;

	@Context
	HttpServletRequest request;
	
	@GET
	public ResultList<Vote> getAll() {
		int page = NumberUtils.toInt(request.getParameter("page"), Constants.DEFAULT_PAGE);
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), Constants.DEFAULT_PAGE_SIZE);
		return voteDao.listWithTotalPages(page, pageSize);
	}

	@GET
	@Path("{id}")
	public Vote getById(@PathParam("id") long id) {
		return voteDao.findById(id);
	}
	
	@GET
	@Path("{articleId}/counts")
	public VoteCounts getVoteCount(@PathParam("articleId") long articleId) {
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("articleId", articleId);
		args.put("favorable", true);
		int favorableCount = (int)voteDao.count(args);
		
		args = new HashMap<String,Object>();
		args.put("articleId", articleId);
		args.put("favorable", false);
		int againstCount = (int)voteDao.count(args);
		
		return new VoteCounts(favorableCount, againstCount);
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
		ResultList<Vote> list = voteDao.findByColumnsWithTotalPages(args);
		
		return list.getObjects().isEmpty() ? null : list.getObjects().get(0);
	}
	
	@POST
	public Response createVote(final Vote vote) {
		Vote existing = getByArticleIdAndVisitorId(vote.getArticleId(), vote.getVisitorId());
		if (existing != null) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(String.format("Visitor id %s has already voted for article %d. Send a PUT request to modify", 
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
		Vote result = voteDao.save(vote);
		return Response.ok(result).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteVote(@PathParam("id") long id) {
		voteDao.deleteBy("id", id);
		return Response.ok().build();
	}
		
}
