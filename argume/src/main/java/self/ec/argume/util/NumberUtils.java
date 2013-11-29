package self.ec.argume.util;

public class NumberUtils {

	private NumberUtils() {}
	
	public static int toInt(String number, int defaultValue) {
		int result = defaultValue;
		if (number != null && number.trim().length() > 0) {
			try {
				result = Integer.parseInt(number);
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		return result;
	}
}
