package com.abe.olasihaber.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.abe.olasihaber.model.Argument;
import com.abe.olasihaber.model.ResultList;

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
		arg.setId(((BigInteger)row[0]).longValue());
		arg.setArticleId(((BigInteger)row[1]).longValue());
		arg.setSummary((String)row[2]);
		arg.setBody((String)row[3]);
		arg.setAffirmative((Boolean)row[4]);
		arg.setStatus(Argument.Status.values()[(Integer)row[5]]);	
		arg.setLikes(getIntegerFromColumnValue(row[6]));
		arg.setDislikes(getIntegerFromColumnValue(row[7]));
		
		return arg;
	}
	
	public ResultList<Argument> getArgumentsByType(long articleId, String type, int limit) {
		Boolean aff = null;
		if ("supporting".equals(type)) {
			aff = true;
		} else if ("opposed".equals(type)) {
			aff = false;
		}
		
		Map<Long, Argument> result = new HashMap<Long, Argument>();
		
		String q1 = "select a.id, a.article_id, a.summary, a.body, a.is_affirmative, a.status, count(*) as likes, 0 as dislikes "
					+ "from arguments a left outer join likes l on a.id = l.argument_id where l.is_favorable = true"
					+ " and a.article_id = (?1) and a.is_affirmative = (?2) group by a.id";
		
		String q2 = "select a.id, a.article_id, a.summary, a.body, a.is_affirmative, a.status, 0 as likes, count(*) as dislikes "
					+ "from arguments a left outer join likes l on a.id = l.argument_id where l.is_favorable = false "
					+ "and a.article_id = (?1) and a.is_affirmative = (?2) group by a.id";
		
		String q3 = "select a.id, a.article_id, a.summary, a.body, a.is_affirmative, a.status, 0 as likes, 0 as dislikes "
					+ "from arguments a left outer join likes l on a.id = l.argument_id where "
					+ " a.article_id = (?1) and a.is_affirmative = (?2)";
		
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
