package self.ec.argume.util;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;


public class JerseyUtils {

	private JerseyUtils(){}
	
	public static String getEntityFromResponse(final Response response) throws IOException {
	    String entity = null;
	    InputStream in = null;
	    try {
	      in = response.readEntity(InputStream.class);
	      entity = IOUtils.toString(in);
	    } finally {
	      IOUtils.closeQuietly(in);
	    }
	    return entity;
	  }
}
