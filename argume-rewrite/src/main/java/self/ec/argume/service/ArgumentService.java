package self.ec.argume.service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import self.ec.argume.dao.Criteria;
import self.ec.argume.dao.DaoFactory;
import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.Argument;
import self.ec.argume.util.Messages;

public class ArgumentService {

	private static final GenericDao<Argument> argumentDao = DaoFactory.getArgumentDao();
	
	private ArgumentService() {}

	public static Argument createArgument(Argument argument, Integer maxAllowed) {
		if (maxAllowed != null) {
			int count = (int)argumentDao.count(new Criteria().addColumn("articleId", argument.getArticleId())
									.addColumn("affirmative", argument.isAffirmative())
									.addColumn("status", Argument.Status.APPROVED));
			if (count >= maxAllowed) {
				throw new WebApplicationException(Response.status(Status.CONFLICT)
							.entity(Messages.getMessage("errors.arguments.maxReached")).build());
			}
		}
		
		return saveArgument(argument, true);
	}
	
	public static Argument updateArgument(long id, Argument argument) {
		Argument existing = argumentDao.findById(id);
		if (existing == null) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}
		
		argument.setId(id);	
		argument.setUserId(existing.getUserId());
		argument.setDateCreated(existing.getDateCreated());
		
		return saveArgument(argument, false);
	}
	
	private static Argument saveArgument(Argument argument, boolean isNew) {
		if (isNew) {
			argument.setId(null);
			argument.setDateCreated(System.currentTimeMillis());
		} else {
			argument.setDateModified(System.currentTimeMillis());
		}
		
		return argumentDao.save(argument);
	}

	public static void deleteArgument(long id) {
		argumentDao.deleteBy("id", id);
	}
}
