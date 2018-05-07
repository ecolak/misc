package emre.colak.leftoverpolice.lambda;

import emre.colak.leftoverpolice.service.ILeftoverService;
import emre.colak.leftoverpolice.service.PostgresBackedLeftoverService;

public class Utils {

  private Utils() {}
  
  private static final String DB_HOST = "db_host";
  private static final String DB_USER = "db_user";
  private static final String DB_PWD = "db_pwd";
  private static final String DB_NAME = "db_name";
  
  static ILeftoverService dbService() {
    return new PostgresBackedLeftoverService(
        System.getenv(DB_HOST), System.getenv(DB_NAME), System.getenv(DB_USER), System.getenv(DB_PWD));
  }
}
