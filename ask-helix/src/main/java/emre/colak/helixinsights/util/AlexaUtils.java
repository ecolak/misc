package emre.colak.helixinsights.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlexaUtils {

  private AlexaUtils() {}
  
  private static final Set<String> acronyms = new HashSet<>();
  static {
    acronyms.add("DNA");
    acronyms.add("ACG");
  }
  
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
      sb.append("<p>").append(separateAcronyms(t.toString())).append("</p>");
    }
    return sb.toString();
  }
  
  private static String separateAcronyms(String s) {
    for (String acr : acronyms) {
      if (s.equalsIgnoreCase(acr)) {
        return s;
      }
      if (s.startsWith(acr)) {
        s = s.replace(acr, acr + " ");
      } else if (s.endsWith(acr)) {
        s = s.replace(acr, " " +  acr);
      } else if (s.contains(acr)) {
        s = s.replace(acr, " " +  acr + " ");
      }
    }
    return s;
  }
}
