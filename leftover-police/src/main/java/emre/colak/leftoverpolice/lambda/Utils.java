package emre.colak.leftoverpolice.lambda;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import emre.colak.leftoverpolice.model.Leftover;
import emre.colak.leftoverpolice.service.DynamoDBBackedLeftoverService;
import emre.colak.leftoverpolice.service.ILeftoverService;

public class Utils {

  private Utils() {}
  
  private static final String DB_HOST = "db_host";
  private static final String DB_USER = "db_user";
  private static final String DB_PWD = "db_pwd";
  private static final String DB_NAME = "db_name";
  
  static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
  static {
    dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
  }
  
  public static ILeftoverService dbService() {
    //return new PostgresBackedLeftoverService(
      //  System.getenv(DB_HOST), System.getenv(DB_NAME), System.getenv(DB_USER), System.getenv(DB_PWD));
    return new DynamoDBBackedLeftoverService();
  }
  
  public static String leftoverToString(Leftover lo) {
    StringBuilder sb = new StringBuilder();
    sb.append(lo.getName());
    String source = lo.getSource();
    if (source != null) {
      sb.append(" from ").append(source);
    }
    return sb.toString();
  }
  
  public static String leftoversToSSML(List<Leftover> list) {
    if (list.size() == 1) {
      return leftoverToString(list.get(0));
    }
    StringBuilder sb = new StringBuilder();
    int i = 1;
    for (Iterator<Leftover> iter = list.iterator(); iter.hasNext(); i++) {
      sb.append("<p>").append(i).append("<break strength=\"medium\"/>")
        .append(leftoverToString(iter.next()))
        .append("</p>");
    }
    return sb.toString();
  }
}
