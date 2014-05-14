package self.ec.argume.app;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig extends ResourceConfig {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);
	
	public ApplicationConfig() {
		packages("self.ec.argume").register(JacksonFeature.class).register(MustacheMvcFeature.class).
		property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "/html");

		LOG.info("Registering Hibernate Jasypt encryptor");
		registerJasyptEncryptor();
	}

	private void registerJasyptEncryptor() {
		EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
		config.setAlgorithm("PBEWITHMD5ANDDES");
		config.setPasswordEnvName("DB_PWD_ENC_SECRET");

		StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor();
		strongEncryptor.setConfig(config);

		HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry
				.getInstance();
		registry.registerPBEStringEncryptor("hibernateEncryptor",
				strongEncryptor);
	}
}
