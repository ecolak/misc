package self.ec.btcbots.util;

import org.quartz.JobDataMap;

public class JobUtil {

	private JobUtil() {}
	
	public static Float getFloatFromJobDataMap(JobDataMap map, String key) {
		if (map.containsKey(key)) {
			try {
				return map.getFloat(key); 
			} catch (Exception e) {
				// ignore
			}
		}
		return null;
	}
	
	public static float getFloatFromJobDataMap(JobDataMap map, String key, float defaultValue) {
		if (map.containsKey(key)) {
			try {
				return map.getFloat(key); 
			} catch (Exception e) {
				// ignore
			}
		}
		return defaultValue;
	}
	
	public static String getStringFromJobDataMap(JobDataMap map, String key) {
		return getStringFromJobDataMap(map, key, null);
	}
	
	public static String getStringFromJobDataMap(JobDataMap map, String key, String defaultValue) {
		String result = null;
		try {
			result = map.getString(key);
		} catch (Exception e) {
			// ignore
		}
		return result != null ? result : defaultValue;
	}
}
