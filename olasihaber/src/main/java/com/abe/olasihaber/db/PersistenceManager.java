package com.abe.olasihaber.db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceManager {

  private static final Logger LOG = LoggerFactory.getLogger(PersistenceManager.class);
  
  // Use h2 as default persistence unit
  private static String persistenceUnit = "h2";
  static {
    final String db = System.getenv("DB");
    LOG.debug("ENV VAR db: " + db);
    
    if (db != null) {
      persistenceUnit = db;
    }
  }
  
  private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);

  public static EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }

}