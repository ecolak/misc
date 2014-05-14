package self.ec.argume.main;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import self.ec.argume.dao.GenericDao;
import self.ec.argume.model.User;
import self.ec.argume.model.User.Role;

public class CreateUser {

	private static final String[] firstNames = new String[]{"Lionel", "Cristiano", "Gareth", "Sergio", "Manuel"};
	private static final String[] lastNames = new String[]{"Messi", "Ronaldo", "Bale", "Ramos", "Fernandes"};
	private static final Random random = new Random();
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Please provide username and password");
			return;
		}
		
		String email = args[0];
		String password = args[1];
		
		Role role = Role.REGULAR;
		if (args.length > 2) {
			role = Role.valueOf(args[2]);
		}
		
		String passwordSalt = RandomStringUtils.randomAlphabetic(32);
		String saltedPassword = passwordSalt + password;
		String passwordHash = DigestUtils.sha256Hex(saltedPassword.getBytes());
		
		GenericDao<User> userDao = new GenericDao<User>(User.class);
		User user = new User(email, passwordHash, passwordSalt, 
							firstNames[random.nextInt(firstNames.length)], 
							lastNames[random.nextInt(lastNames.length)], role);
		user.setDateCreated(System.currentTimeMillis());
		Long userId = userDao.save(user).getId();
		System.out.println("User created successfully. Id: " + userId);
	}
}