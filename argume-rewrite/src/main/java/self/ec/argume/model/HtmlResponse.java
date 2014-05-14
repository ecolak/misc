package self.ec.argume.model;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import self.ec.argume.util.ContentUtils;

public class HtmlResponse {

	private Map<String,String> meta = new HashMap<>();
	private Object content;
	private Map<String,Object> session = new HashMap<>();
	
	public HtmlResponse() {}
	
	public HtmlResponse(Map<String, String> meta, Object content, Map<String,Object> session) {
		this.meta = meta;
		this.content = content;
		this.session = session;
	}
	
	public HtmlResponse(HttpServletRequest request) {
		this(ContentUtils.getDefaultMeta(request), null, request);
	}
	
	public HtmlResponse(Object content, HttpServletRequest request) {
		this(ContentUtils.getDefaultMeta(request), content, request);
	}
	
	public HtmlResponse(Map<String, String> meta, Object content, HttpServletRequest request) {
		this.meta = meta;
		this.content = content;
		HttpSession httpSession = request.getSession(false);
		if (httpSession != null) {
			for (Enumeration<String> attrNames = httpSession.getAttributeNames(); attrNames.hasMoreElements(); ) {
				String attrName = attrNames.nextElement();
				this.session.put(attrName, httpSession.getAttribute(attrName));
			} 
		}
	}
	
	public Map<String, String> getMeta() {
		return meta;
	}
	public void setMeta(Map<String, String> meta) {
		this.meta = meta;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
		
}