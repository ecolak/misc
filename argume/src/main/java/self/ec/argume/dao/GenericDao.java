package self.ec.argume.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import self.ec.argume.db.PersistenceManager;
import self.ec.argume.model.ResultList;

/**
 * Generic class to help create data access objects
 * */
public class GenericDao<T> {

	protected Class<T> entityClass;
	protected String entityName;
	protected EntityManagerFactory emf;

	/**
	 * Constructor that creates an entity manager
	 * 
	 * @param entityClass - class of the entity
	 * */
	public GenericDao(Class<T> entityClass) {
		this(entityClass, null);
	}

	/**
	 * Constructor
	 * 
	 * @param entityClass - class of the entity
	 * @param emf - entity manager factory, this class will create one if this is null
	 * */
	public GenericDao(Class<T> entityClass, EntityManagerFactory emf) {
		if (entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		}

		this.entityClass = entityClass;
		this.entityName = entityClass.getSimpleName();
		this.emf = (emf == null ? PersistenceManager.getEntityManagerFactory() : emf);
	}

	/**
	 * Given an id, retrieves a single result matching the id
	 * 
	 * @param id
	 *            - entity id
	 * @return unique entity
	 * */
	public T findById(final Object id) {
		T result = null;
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			result = em.find(entityClass, id);
		} finally {
			if (em != null) em.close();
		}
		return result;
	}
	
	public long count(Criteria criteria) {
		EntityManager em = null;
		long count = 0;
		try {
			em = emf.createEntityManager();
			
			StringBuilder qb = new StringBuilder("select count(*) as cnt from " + entityName);
			int i = 0;
			for (Criteria.Condition condition : criteria.getCriteria()) {
				if (i > 0) {
					qb.append(" and ");
				} else {
					qb.append(" where ");
				}
		
				qb.append(condition.getColumnName()).append(" ").append(condition.getOperator().getSymbol())
				  .append(" :").append(condition.getColumnName());
				i++;
			}

			final Query query = em.createQuery(qb.toString());
			for (Criteria.Condition condition : criteria.getCriteria()) {
				query.setParameter(condition.getColumnName(), condition.getColumnValue());
			}
			
			count = (Long) query.getSingleResult();
		} finally {
			if (em != null) em.close();
		}
		
		return count;
	}
	
	public ResultList<T> query(Criteria criteria) {
		List<T> rows = new ArrayList<T>();
		EntityManager em = null;
		long totalRows = 0;
		try {
			em = emf.createEntityManager();
			
			if (criteria.isPaged()) {
				final Query countQuery = em.createQuery("select count(*) as cnt from " + entityName);
				totalRows = (Long) countQuery.getSingleResult(); 
			}
			
			StringBuilder qb = new StringBuilder("from " + entityName);
			int i = 0;
			for (Criteria.Condition condition : criteria.getCriteria()) {
				if (i > 0) {
					qb.append(" and ");
				} else {
					qb.append(" where ");
				}
		
				qb.append(condition.getColumnName()).append(" ").append(condition.getOperator().getSymbol())
				  .append(" :").append(condition.getColumnName());
				i++;
			}
			
			if (criteria.getOrderBy() != null) {
				qb.append(" order by ").append(criteria.getOrderBy());
			}

			final TypedQuery<T> query = criteria.isPaged() 
					? getQuery(em, qb.toString(), criteria.getPage(), criteria.getPageSize()) 
					: getQuery(em, qb.toString(), 0, 0);
			
			for (Criteria.Condition condition : criteria.getCriteria()) {
				query.setParameter(condition.getColumnName(), condition.getColumnValue());
			}
			
			rows = query.getResultList();
		} finally {
			if (em != null) em.close();
		}
		
		ResultList<T> result = new ResultList<T>();
		result.setObjects(rows);
		
		if (criteria.isPaged()) {
			result.setPage(criteria.getPage());
			result.setPageSize(criteria.getPageSize());
			result.setTotal(totalRows);
		}
		
		return result;
	}
	
	public List<T> findBy(String columnName, Object columnValue) {
		return query(new Criteria().addColumn(columnName, columnValue)).getObjects();
	}

	/**
	 * Persists a given entity (model)
	 * 
	 * @param model - entity to persist
	 * @return T - saved model
	 * */
	public T save(final T model) {
		T result = null;
		EntityManager em = null;
		EntityTransaction transaction = null;
		try {
			em = emf.createEntityManager();
			transaction = em.getTransaction();
			transaction.begin();
			result = em.merge(model);
			transaction.commit();
		} catch (RuntimeException e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (em != null) em.close();
		}
		return result;
	}

	/**
	 * Deletes a given entity (model)
	 * 
	 * @param model - entity to persist
	 * */
	public void delete(final T model) {
		EntityManager em = null;
		EntityTransaction transaction = null;

		try {
			em = emf.createEntityManager();
			transaction = em.getTransaction();
			transaction.begin();
			em.remove(model);
			transaction.commit();
		} catch (RuntimeException e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (em != null) em.close();
		}
	}

	/**
	 * Given a column name and a column value, deletes all matching entities
	 * @param columnName - name of column to delete by
	 * @param columnValue - value for columnName to delete by
	 * @return - number of row deleted
	 * */
	public int deleteBy(final String columnName, final Object columnValue) {
		int result = 0;
		EntityManager em = null;
		EntityTransaction transaction = null;
		try {
			em = emf.createEntityManager();
			transaction = em.getTransaction();
			transaction.begin();
			Query query = em.createQuery("delete from " + entityName + " where " + columnName + " = :" + columnName);
			query.setParameter(columnName, columnValue);
			result = query.executeUpdate();
			transaction.commit();
		} catch (RuntimeException e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		} finally {
			if (em != null) em.close();
		}
		return result;
	}
	
	private TypedQuery<T> getQuery(final EntityManager em, final String query, 
			final int page, final int pageSize) {
		TypedQuery<T> result = em.createQuery(query, entityClass);
		if (page > 0) {
			result.setFirstResult((page - 1) * pageSize);
		}
		if (pageSize > 0) {
			result.setMaxResults(pageSize);
		}
		return result;
	}

}