package self.ec.argume.db;

import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.exceptions.EncryptionInitializationException;
import org.jasypt.hibernate4.connectionprovider.ParameterNaming;
import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;
import org.jasypt.properties.PropertyValueEncryptionUtils;

/**
 * Class is necessary due to a bug in EncryptedPasswordC3P0ConnectionProvider
 * */
public class CustomC3P0ConnectionProvider extends C3P0ConnectionProvider {

	private static final long serialVersionUID = -8925794348441488310L;
    
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
    public void configure(final Map props) {
       final String encryptorRegisteredName = 
           (String)props.get(ParameterNaming.ENCRYPTOR_REGISTERED_NAME);
       
       final HibernatePBEEncryptorRegistry encryptorRegistry =
           HibernatePBEEncryptorRegistry.getInstance();
       final PBEStringEncryptor encryptor = 
           encryptorRegistry.getPBEStringEncryptor(encryptorRegisteredName);
       
       if (encryptor == null) {
           throw new EncryptionInitializationException(
                   "No string encryptor registered for hibernate " +
                   "with name \"" + encryptorRegisteredName + "\"");
       }

       // Get the original values, which may be encrypted
       final String driver = (String)props.get(AvailableSettings.DRIVER);
       final String url = (String)props.get(AvailableSettings.URL);
       final String user = (String)props.get(AvailableSettings.USER);
       final String password = (String)props.get(AvailableSettings.PASS);

       // Perform decryption operations as needed and store the new values
       if (PropertyValueEncryptionUtils.isEncryptedValue(driver)) {
           props.put(
                   AvailableSettings.DRIVER, 
                   PropertyValueEncryptionUtils.decrypt(driver, encryptor));
       }
       if (PropertyValueEncryptionUtils.isEncryptedValue(url)) {
           props.put(
                   AvailableSettings.URL, 
                   PropertyValueEncryptionUtils.decrypt(url, encryptor));
       }
       if (PropertyValueEncryptionUtils.isEncryptedValue(user)) {
           props.put(
                   AvailableSettings.USER, 
                   PropertyValueEncryptionUtils.decrypt(user, encryptor));
       }
       if (PropertyValueEncryptionUtils.isEncryptedValue(password)) {
           props.put(
                   AvailableSettings.PASS, 
                   PropertyValueEncryptionUtils.decrypt(password, encryptor));
       } 
       
       // Let Hibernate do the rest
       super.configure(props);
       
    } 
}