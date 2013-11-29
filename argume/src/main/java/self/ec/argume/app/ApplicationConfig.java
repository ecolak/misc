package self.ec.argume.app;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class ApplicationConfig extends ResourceConfig {
	
	public ApplicationConfig() {
        packages("self.ec.argume").register(JacksonFeature.class);
    }

}
