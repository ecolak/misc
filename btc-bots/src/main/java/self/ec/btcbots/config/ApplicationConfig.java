package self.ec.btcbots.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class ApplicationConfig extends ResourceConfig {

  public ApplicationConfig() {
    packages("self.ec.btcbots").register(JacksonFeature.class);
  }
}
