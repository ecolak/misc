package self.ec.argume.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {

	private Messages() {}
	
	private static final ResourceBundle messages = ResourceBundle.getBundle("messages", new Locale("tr", "TR"));
	
	public static String getMessage(String key) {
		String result = "";
		try {
			result = new String(messages.getString(key).getBytes("ISO-8859-1"), "UTF-8"); 
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}
