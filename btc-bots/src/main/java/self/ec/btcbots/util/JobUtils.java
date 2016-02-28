package self.ec.btcbots.util;

import java.util.Map;

public class JobUtils {

  private JobUtils() {}

  public static Float getFloatFromParams(Map<String, Object> map, String key) {
    return getFloatFromParamsInternal(map, key);
  }

  public static float getFloatFromParams(Map<String, Object> map, String key, float defaultValue) {
    Float result = getFloatFromParamsInternal(map, key);
    return result != null ? result.floatValue() : defaultValue;
  }

  private static Float getFloatFromParamsInternal(Map<String, Object> map, String key) {
    Float result = null;
    if (map.containsKey(key)) {
      try {
        Object o = map.get(key);
        if (o instanceof Float) {
          result = (Float) o;
        } else if (o instanceof Double) {
          result = new Float((Double) o);
        }
      } catch (Exception e) {
        // ignore
      }
    }
    return result;
  }

  public static String getStringFromParams(Map<String, Object> map, String key) {
    return getStringFromParams(map, key, null);
  }

  public static String getStringFromParams(Map<String, Object> map, String key, String defaultValue) {
    String result = null;
    if (map.containsKey(key)) {
      try {
        result = (String) map.get(key);
      } catch (Exception e) {
        // ignore
      }
    }
    return result != null ? result : defaultValue;
  }

}
