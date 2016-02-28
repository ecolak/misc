package self.ec.btcbots.db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceManager {

  private static final Logger LOG = LoggerFactory.getLogger(PersistenceManager.class);

  // Use h2 as default persistence unit
  private static String persistenceUnit = "h2";
  static {
    String db = System.getProperty("DB");
    if (db == null) {
      db = System.getenv("DB");
    }

    if (db != null) {
      persistenceUnit = db;
    }

    LOG.info("Using persistence unit " + persistenceUnit);
  }

  private final static EntityManagerFactory entityManagerFactory = Persistence
      .createEntityManagerFactory(persistenceUnit);

  public static EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }

}
