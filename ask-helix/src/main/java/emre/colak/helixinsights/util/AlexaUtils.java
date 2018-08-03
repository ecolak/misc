package emre.colak.helixinsights.util;

import java.util.List;

public class AlexaUtils {

  private AlexaUtils() {}
  
  public static <T> String listToSSML(List<T> list) {
    if (list.size() == 1) {
      return list.get(0).toString();
    }
    StringBuilder sb = new StringBuilder();
    /*int i = 1;
    for (Iterator<T> iter = list.iterator(); iter.hasNext(); i++) {
      sb.append("<p>").append(i).append("<break strength=\"medium\"/>")
        .append(iter.next().toString())
        .append("</p>");
    }*/
    for (T t : list) {
      sb.append("<p>").append(t.toString()).append("</p>");
    }
    return sb.toString();
  }
}
