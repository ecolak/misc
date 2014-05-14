package self.ec.argume.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ContentUtils {

	public static final String ARGUME = "Argume";
	public static final String DEFAULT_KEYWORDS = "Yalan haber, argüman, tartışma, dezenformasyon";
	public static final String DEFAULT_DESCRIPTION = "Yalan haberleri ortaya çıkarmayı, doğru bilgilere ulaşmayı, "
			+ "açık görüşlü ve seviyeli bir tartışma ortamı yaratmayı hedefleyen platform";
	public static final String DEFAULT_IMG_PATH = "/img/argume_transparent.gif";
	public static final int DEFAULT_HTTP_PORT = 80;
	public static final int DEFAULT_HTTPS_PORT = 443;
	
	private ContentUtils() {}
	
	public static StringBuilder getHost(HttpServletRequest req) {
	    String scheme = req.getScheme();        
	    String serverName = req.getServerName(); 
	    int serverPort = req.getServerPort();        

	    StringBuilder url =  new StringBuilder();
	    url.append(scheme).append("://").append(serverName);

	    if ((serverPort != DEFAULT_HTTP_PORT) && (serverPort != DEFAULT_HTTPS_PORT)) {
	        url.append(":").append(serverPort);
	    }
	    
	    return url;
	}
	
	public static Map<String,String> getDefaultMeta(HttpServletRequest request) {
		Map<String,String> result = new HashMap<>();
		result.put("keywords", DEFAULT_KEYWORDS);
		result.put("description", DEFAULT_DESCRIPTION);
		result.put("title", ARGUME);
		result.put("image", getHost(request).append(DEFAULT_IMG_PATH).toString());
		result.put("url", request.getRequestURL().toString());
		
		return result;
	}
	
}
