package emre.colak.helixinsights.util;

import java.util.List;

public class AlexaUtils {

  private AlexaUtils() {}
  
  public static <T> String listToSSML(List<T> list) {
    return listToSSML(list, null);
  }
  
  public static <T> String listToSSML(List<T> list, String title) {
    if (list.size() == 1) {
      return list.get(0).toString();
    }
    StringBuilder sb = new StringBuilder();
    if (title != null) {
      sb.append("<p>").append(title).append("</p>");
    }
    for (T t : list) {
      sb.append("<p>").append(t.toString()).append("</p>");
    }
    return sb.toString();
  }
}
