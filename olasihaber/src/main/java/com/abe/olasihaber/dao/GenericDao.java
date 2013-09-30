package com.abe.olasihaber.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.abe.olasihaber.db.PersistenceManager;
import com.abe.olasihaber.model.ResultList;

/**
 * Generic class to help create data access objects
 * */
public class GenericDao<T> {

	public static enum Operand {
		EQ("="), NEQ("!="), GT(">"), GTE(">="), LT("<"), LTE("<=");
		
		private final String symbol;
		Operand(String symbol) {
			this.symbol = symbol;
		}
		
		public String getSymbol() { return symbol; }
	}
	
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

	/**
	 * Retrieves all entities
	 *
	 * @return list of entities plus total pages
	 * */
	public ResultList<T> listWithTotalPages() {
		return listWithTotalPages(0, 0);
	}
	
	/**
	 * Retrieves all entities
	 * 
	 * @param pageSize
	 * @return list of entities plus total pages
	 * */
	public ResultList<T> listWithTotalPages(final int pageSize) {
		return listWithTotalPages(0, pageSize);
	}
	
	/**
	 * Retrieves all entities
	 * 
	 * @param page
	 * @param pageSize
	 * @return list of entities plus total pages
	 * */
	public ResultList<T> listWithTotalPages(final int page, final int pageSize) {
		return listInternal(page, pageSize, true);
	}
	
	public List<T> list() {
		return listInternal(0, 0, false).getObjects();
	}
	
	public List<T> list(final int pageSize) {
		return listInternal(0, pageSize, false).getObjects();
	}
	
	public List<T> list(final int page, final int pageSize) {
		return listInternal(page, pageSize, false).getObjects();
	}
	
	private ResultList<T> listInternal(final int page, final int pageSize, boolean countTotalPages) {
		List<T> rows = new ArrayList<T>();
		EntityManager em = null;
		long totalRows = 0;
		try {
			em = emf.createEntityManager();
			if (countTotalPages) {
				final Query countQuery = em.createQuery("select count(*) from " + entityName);
				totalRows = (Long) countQuery.getSingleResult();
			}
			
			final TypedQuery<T> query = getQuery(em, "from " + entityName, page, pageSize);
			rows = query.getResultList();
		} finally {
			if (em != null) em.close();
		}
		
		ResultList<T> result = new ResultList<T>();
		result.setObjects(rows);
		
		if (countTotalPages) {
			result.setPage(page);
			result.setPageSize(pageSize);
			result.setTotal(totalRows);
		}
		
		return result;
	}

	public ResultList<T> findByColumnWithTotalPages(final String columnName, final Object columnValue) {
		return findByColumnWithTotalPages(columnName, columnValue, Operand.EQ, 0, 0);
	}
	
	public ResultList<T> findByColumnWithTotalPages(final String columnName, final Object columnValue, final Operand operand) {
		return findByColumnWithTotalPages(columnName, columnValue, operand, 0, 0);
	}
	
	public ResultList<T> findByColumnWithTotalPages(final String columnName, final Object columnValue, final Operand operand, final int pageSize) {
		return findByColumnWithTotalPages(columnName, columnValue, operand, 0, pageSize);
	}
	
	/**
	 * Given a column name and a value for that column, retrieves the list of
	 * matching entities
	 * 
	 * @param columnName
	 * @param columnValue
	 * @param pageSize
	 * @return list of entities plus total pages
	 * */
	public ResultList<T> findByColumnWithTotalPages(final String columnName, final Object columnValue, final Operand operand, final int page, final int pageSize) {
		return findByInternal(columnName, columnValue, operand, page, pageSize, true);
	}
	
	public List<T> findBy(final String columnName, final Object columnValue) {
		return findByInternal(columnName, columnValue, Operand.EQ, 0, 0, false).getObjects();
	}
	
	public List<T> findBy(final String columnName, final Object columnValue, final Operand operand) {
		return findByInternal(columnName, columnValue, operand, 0, 0, false).getObjects();
	}
	
	public List<T> findBy(final String columnName, final Object columnValue, final Operand operand, final int pageSize) {
		return findByInternal(columnName, columnValue, operand, 0, pageSize, false).getObjects();
	}
	
	public List<T> findBy(final String columnName, final Object columnValue, final Operand operand, final int page, final int pageSize) {
		return findByInternal(columnName, columnValue, operand, page, pageSize, false).getObjects();
	}
	
	/**
	 * Given a column name and a value for that column, retrieves the list of
	 * matching entities
	 * 
	 * @param columnName
	 * @param columnValue
	 * @param page
	 * @param pageSize
	 * @return list of entities plus total pages
	 * */
	private ResultList<T> findByInternal(final String columnName, final Object columnValue, final Operand operand, final int page, final int pageSize, boolean countTotalPages) {
		List<T> rows = new ArrayList<T>();
		EntityManager em = null;
		long totalRows = 0;
		try {
			em = emf.createEntityManager();
			String queryWoProjection = "from " + entityName + " where " + columnName + " " + operand.getSymbol() + " :" + columnName;
			if (countTotalPages) {
				final Query countQuery = em.createQuery("select count(*) as cnt " + queryWoProjection);
				countQuery.setParameter(columnName, columnValue);
				totalRows = (Long) countQuery.getSingleResult(); 
			}
			
			final TypedQuery<T> query = getQuery(em, queryWoProjection, page, pageSize);
			query.setParameter(columnName, columnValue);
			rows = query.getResultList();
		} finally {
			if (em != null) em.close();
		}
		
		ResultList<T> result = new ResultList<T>();
		result.setObjects(rows);
		
		if (countTotalPages) {
			result.setPage(page);
			result.setPageSize(pageSize);
			result.setTotal(totalRows);
		}
		
		return result;
	}
	
	/**
	 * Given a map of column name to column values, retrieves the list of
	 * matching entities for all entries in the map
	 * 
	 * @param columnMap - map of column keys to values e.g. {"id": 3, "name": "abc"}
	 * @return List of entities plus total pages
	 * */
	public ResultList<T> findByColumnsWithTotalPages(final Map<String, Object> columnMap) {
		return findByColumnsWithTotalPages(columnMap, 0, 0);
	}
	
	public ResultList<T> findByColumnsWithTotalPages(final Map<String, Object> columnMap, final int pageSize) {
		return findByColumnsWithTotalPages(columnMap, 0, pageSize);
	}
	
	public ResultList<T> findByColumnsWithTotalPages(final Map<String, Object> columnMap, final int page, final int pageSize) {
		return findByColumnsInternal(columnMap, page, pageSize, true);
	}
	
	public List<T> findByColumns(final Map<String, Object> columnMap) {
		return findByColumns(columnMap, 0, 0);
	}

	public List<T> findByColumns(final Map<String, Object> columnMap, final int pageSize) {
		return findByColumns(columnMap, 0, pageSize);
	}
	
	public List<T> findByColumns(final Map<String, Object> columnMap, final int page, final int pageSize) {
		return findByColumnsInternal(columnMap, page, pageSize, false).getObjects();
	}
	
	/**
	 * Given a map of column name to column values, retrieves the list of
	 * matching entities for all entries in the map
	 * 
	 * @param columnMap - map of column keys to values e.g. {"id": 3, "name": "abc"}
	 * @param page
	 * @param pageSize
	 * @return List of entities plus total pages
	 * */
	private ResultList<T> findByColumnsInternal(final Map<String, Object> columnMap, final int page, final int pageSize, final boolean countTotalPages) {
		List<T> rows = new ArrayList<T>();
		EntityManager em = null;
		long totalRows = 0;
		try {
			em = emf.createEntityManager();
			if (countTotalPages) {
				final Query countQuery = em.createQuery("select count(*) as cnt from " + entityName);
				totalRows = (Long) countQuery.getSingleResult(); 
			}
			
			StringBuilder qb = new StringBuilder("from " + entityName + " where ");
			int i = 0;
			for (Entry<String, Object> entry : columnMap.entrySet()) {
				if (i > 0) {
					qb.append(" and ");
				}
				String columnName = entry.getKey();
				qb.append(columnName).append(" = :").append(columnName);
				i++;
			}

			final TypedQuery<T> query = getQuery(em, qb.toString(), page, pageSize);
			for (Entry<String, Object> entry : columnMap.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}

			rows = query.getResultList();
		} finally {
			if (em != null) em.close();
		}
		
		ResultList<T> result = new ResultList<T>();
		result.setObjects(rows);
		
		if (countTotalPages) {
			result.setPage(page);
			result.setPageSize(pageSize);
			result.setTotal(totalRows);
		}
		
		return result;
	}
	
	public long count(final String columnName, final Object columnValue) {
		long result = 0;
		EntityManager em = null;
		try {
			em = emf.createEntityManager();			
			String q = "select count(*) as cnt from " + entityName;
			boolean hasColumns = (columnName != null && columnValue != null);
			if (hasColumns) {
				q += " where " + columnName + " = :" + columnName;
			}
			final Query query = em.createQuery(q);
			
			if (hasColumns) {
				query.setParameter(columnName, columnValue);
			}
			result = (Long) query.getSingleResult();
		} finally {
			if (em != null) em.close();
		}
		
		return result;
	}
	
	public long count(final Map<String, Object> columnMap) {
		long result = 0;
		EntityManager em = null;
		try {
			em = emf.createEntityManager();			
			StringBuilder qb = new StringBuilder("select count(*) as cnt from " + entityName + " where ");
			int i = 0;
			for (Entry<String, Object> entry : columnMap.entrySet()) {
				if (i > 0) {
					qb.append(" and ");
				}
				String columnName = entry.getKey();
				qb.append(columnName).append(" = :").append(columnName);
				i++;
			}

			final Query query = em.createQuery(qb.toString());
			for (Entry<String, Object> entry : columnMap.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
			
			result = (Long) query.getSingleResult();
		} finally {
			if (em != null) em.close();
		}
		
		return result;
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