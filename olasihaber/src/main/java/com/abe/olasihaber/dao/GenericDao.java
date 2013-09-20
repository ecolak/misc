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
   * @param em - entity manager, class will create one if em is null
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
   * @param id - entity id
   * @return unique entity
   * */
  public T findById(final Object id) {
    T result = null;
    final EntityManager em = emf.createEntityManager();
    result = em.find(entityClass, id);
    em.close();
    return result;
  }

  /**
   * Retrieves all entities
   * 
   * @return list of entities
   * */
  public List<T> findAll() {
    List<T> result = new ArrayList<T>();
    final EntityManager em = emf.createEntityManager();
    final TypedQuery<T> query = em.createQuery("from " + entityName, entityClass);
    result = query.getResultList();
    em.close();
    return result;
  }

  /**
   * Given a column name and a value for that column, retrieves the list of matching entities
   * 
   * @param columnName
   * @param columnValue
   * @return list of entities
   * */
  public List<T> findBy(final String columnName, final Object columnValue) {
    List<T> result = new ArrayList<T>();
    final EntityManager em = emf.createEntityManager();
    final TypedQuery<T> query = em.createQuery("from " + entityName + " where " + columnName + " = :" + columnName,
                                               entityClass);
    query.setParameter(columnName, columnValue);
    result = query.getResultList();
    em.close();
    return result;
  }

  /**
   * Given a map of column name to column values, retrieves the list of matching entities for all
   * entries in the map
   * 
   * @param columnMap - map of column keys to values e.g. {"id": 3, "name": "abc"}
   * */
  public List<T> findBy(final Map<String, Object> columnMap) {
    List<T> result = new ArrayList<T>();
    final EntityManager em = emf.createEntityManager();
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

    final TypedQuery<T> query = em.createQuery(qb.toString(), entityClass);
    for (Entry<String, Object> entry : columnMap.entrySet()) {
      query.setParameter(entry.getKey(), entry.getValue());
    }

    result = query.getResultList();
    em.close();
    return result;
  }

  /**
   * Persists a given entity (model)
   * 
   * @param t - model
   * @return T - saved model
   * */
  public T save(final T model) {
    T result = null;
    final EntityManager em = emf.createEntityManager();
    EntityTransaction transaction = null;
    try {
      transaction = em.getTransaction();
      transaction.begin();
      result = em.merge(model);
      transaction.commit();
    } catch (RuntimeException e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw e;
    }
    em.close(); 
    return result;
  }

  /**
   * Deletes a given entity (model)
   * 
   * @param t - model
   * */
  public void delete(final T model) {
    final EntityManager em = emf.createEntityManager();
    EntityTransaction transaction = null;
    
    try {
      transaction = em.getTransaction();
      transaction.begin();
      em.remove(model);
      transaction.commit();
    } catch (RuntimeException e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw e;
    }
    
    em.close(); 
  }

  /**
   * Given a column name and a column value, deletes all matching entities
   * */
  public int deleteBy(final String columnName, final Object columnValue) {
    int result = 0;
    final EntityManager em = emf.createEntityManager();
    EntityTransaction transaction = null;
    try {
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
    }
    em.close();
    return result;
  }

}
