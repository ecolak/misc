package self.ec.argume.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import self.ec.argume.model.Argument;
import self.ec.argume.model.ResultList;

public class ArgumentDao extends GenericDao<Argument> {

	public ArgumentDao() {
		super(Argument.class);
	}
	
	private int getIntegerFromColumnValue(Object value) {
		int result = 0;
		if (value.getClass().equals(Integer.class)) {
			result = (Integer)value;
		} else if (value.getClass().equals(BigInteger.class)) {
			result = ((BigInteger)value).intValue();
		}
		return result;
	}
	
	private Argument getArgumentFromRow(Object[] row) {
		Argument arg = new Argument();
		if (row.length > 0 && row[0] != null) {
			arg.setId(((BigInteger)row[0]).longValue());
		}
		if (row.length > 1 && row[1] != null) {
			arg.setArticleId(((BigInteger)row[1]).longValue()); 
		}
		if (row.length > 2 && row[2] != null) {
			arg.setUserId(((BigInteger)row[2]).longValue()); 
		}
		if (row.length > 3 && row[3] != null) {
			arg.setSummary((String)row[3]); 
		}
		if (row.length > 4 && row[4] != null) {
			arg.setBody((String)row[4]); 
		}
		if (row.length > 5 && row[5] != null) {
			arg.setAffirmative((Boolean)row[5]); 
		}
		if (row.length > 6 && row[6] != null) {
			arg.setStatus(Argument.Status.valueOf((String)row[6]));	
		}
		if (row.length > 7 && row[7] != null) {
			arg.setDateCreated(((BigInteger)row[7]).longValue()); 
		}
		if (row.length > 8 && row[8] != null) {
			arg.setDateModified(((BigInteger)row[8]).longValue()); 
		}
		if (row.length > 9 && row[9] != null) {
			arg.setLikes(getIntegerFromColumnValue(row[9])); 
		}
		if (row.length > 10 && row[10] != null) {
			arg.setDislikes(getIntegerFromColumnValue(row[10]));
		}
		return arg;
	}
	
	public int getArgumeScoreForUser(long userId) {
		int result = 0;
		String q = "select x.likes_count, y.dislikes_count from (select count(*) as likes_count from arguments a, "
				+ "likes l where a.id = l.argument_id and l.is_favorable = true and a.user_id = (?1)) x, "
				+ "(select count(*) as dislikes_count from arguments a, likes l where a.id = l.argument_id and "
				+ "l.is_favorable = false and a.user_id = (?2)) y";
		
		EntityManager em = null;
		
		try {
			em = emf.createEntityManager();
			Query query = em.createNativeQuery(q);
			query.setParameter(1, userId);
			query.setParameter(2, userId);
			Object[] row = (Object[])query.getSingleResult();
			if (row != null) {
				result = (int)Math.round(Argument.magicScore(getIntegerFromColumnValue(row[0]), getIntegerFromColumnValue(row[1])));
			}
			
		} finally {
			if (em != null) em.close();
		}
		
		return result > 0 ? result : 0;
	}
	
	public ResultList<Argument> getArgumentsForUser(long userId, int limit) {
		Map<Long, Argument> argMap = new HashMap<Long, Argument>();
		String q1 = "select a.id, a.article_id, a.user_id, a.summary, a.body, a.is_affirmative, a.status, "
					+ "a.date_created, a.date_modified, count(*) as likes, 0 as dislikes "
					+ "from arguments a left outer join likes l on a.id = l.argument_id where l.is_favorable = true"
					+ " and a.user_id = (?1) group by a.id";
		
		String q2 = "select a.id, a.article_id, a.user_id, a.summary, a.body, a.is_affirmative, a.status, "
					+ "a.date_created, a.date_modified, 0 as likes, count(*) as dislikes "
					+ "from arguments a left outer join likes l on a.id = l.argument_id where l.is_favorable = false "
					+ "and a.user_id = (?1) group by a.id";
		
		String q3 = "select a.id, a.article_id, a.user_id, a.summary, a.body, a.is_affirmative, a.status, "
					+ "a.date_created, a.date_modified, 0 as likes, 0 as dislikes "
					+ "from arguments a left outer join likes l on a.id = l.argument_id where l.id is null and"
					+ " a.user_id = (?1) group by a.id";
		
		EntityManager em = null;
		
		try {
			em = emf.createEntityManager();
			Query query = em.createNativeQuery(q1);
			query.setParameter(1, userId);
			List rows = query.getResultList();
			for (Object data : rows) {
				Argument arg = getArgumentFromRow((Object[])data);
				argMap.put(arg.getId(), arg);
			}
						
			query = em.createNativeQuery(q2);
			query.setParameter(1, userId);
			rows = query.getResultList();
			for (Object data : rows) {
				Argument arg = getArgumentFromRow((Object[])data);
				Argument argInMap = argMap.get(arg.getId());
				if (argInMap != null) {
					argInMap.setDislikes(arg.getDislikes());
				} else {
					argMap.put(arg.getId(), arg); 
				}
			}
			
			query = em.createNativeQuery(q3);
			query.setParameter(1, userId);
			rows = query.getResultList();
			for (Object data : rows) {
				Argument arg = getArgumentFromRow((Object[])data);
				if (!argMap.containsKey(arg.getId()))
					argMap.put(arg.getId(), arg);
			}
			
		} finally {
			if (em != null) em.close();
		}
		
		List<Argument> result = new ArrayList<Argument>(argMap.values());
		Collections.sort(result, new Comparator<Argument>() {
			@Override
			public int compare(Argument arg0, Argument arg1) {
				Long dc1 = arg0.getDateCreated();
				Long dc2 = arg1.getDateCreated();
				if (dc1 == null && dc2 == null) {
					return 0;
				}
				if (dc1 == null && dc2 != null) {
					return -1;
				}
				if (dc1 != null && dc2 == null) {
					return 1;
				}
				return -(dc1.compareTo(dc2));
			}			
		});
		
		List<Argument> trimmedResult = new ArrayList<Argument>();
		int i = 0;
		for (Argument arg : result) {
			trimmedResult.add(arg);
			i++;
			if (i == limit) break;
		}		
		
		return new ResultList<Argument>(trimmedResult, 1, limit, argMap.size());
	}
	
	public ResultList<Argument> getArgumentsByType(long articleId, String type, int limit) {
		Boolean aff = null;
		if ("supporting".equals(type)) {
			aff = true;
		} else if ("opposed".equals(type)) {
			aff = false;
		}
		
		Map<Long, Argument> result = new HashMap<Long, Argument>();
		
		String q1 = "select a.id, a.article_id, a.user_id, a.summary, a.body, a.is_affirmative, a.status, "
					+ "a.date_created, a.date_modified, count(*) as likes, 0 as dislikes "
					+ "from arguments a left outer join likes l on a.id = l.argument_id where l.is_favorable = true"
					+ " and a.article_id = (?1) and a.is_affirmative = (?2) group by a.id";
		
		String q2 = "select a.id, a.article_id, a.user_id, a.summary, a.body, a.is_affirmative, a.status, "
					+ "a.date_created, a.date_modified, 0 as likes, count(*) as dislikes "
					+ "from arguments a left outer join likes l on a.id = l.argument_id where l.is_favorable = false "
					+ "and a.article_id = (?1) and a.is_affirmative = (?2) group by a.id";
		
		String q3 = "select a.id, a.article_id, a.user_id, a.summary, a.body, a.is_affirmative, a.status, "
					+ "a.date_created, a.date_modified, 0 as likes, 0 as dislikes "
					+ "from arguments a left outer join likes l on a.id = l.argument_id where l.id is null and"
					+ " a.article_id = (?1) and a.is_affirmative = (?2) group by a.id";
		
		EntityManager em = null;
		
		try {
			em = emf.createEntityManager();
			Query query = em.createNativeQuery(q1);
			query.setParameter(1, articleId);
			query.setParameter(2, aff);
			List rows = query.getResultList();
			for (Object data : rows) {
				Argument arg = getArgumentFromRow((Object[])data);
				result.put(arg.getId(), arg);
			}
						
			query = em.createNativeQuery(q2);
			query.setParameter(1, articleId);
			query.setParameter(2, aff);
			rows = query.getResultList();
			for (Object data : rows) {
				Argument arg = getArgumentFromRow((Object[])data);
				result.put(arg.getId(), arg);
			}
			
			query = em.createNativeQuery(q3);
			query.setParameter(1, articleId);
			query.setParameter(2, aff);
			rows = query.getResultList();
			for (Object data : rows) {
				Argument arg = getArgumentFromRow((Object[])data);
				if (!result.containsKey(arg.getId()))
					result.put(arg.getId(), arg);
			}
			
		} finally {
			if (em != null) em.close();
		}
		
		List<Argument> trimmedResult = new ArrayList<Argument>();
		int i = 0;
		for (Argument arg : result.values()) {
			trimmedResult.add(arg);
			i++;
			if (i == limit) break;
		}
		return new ResultList<Argument>(trimmedResult, 1, limit, result.size());
	}
}
