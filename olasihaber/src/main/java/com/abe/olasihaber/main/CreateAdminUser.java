package com.abe.olasihaber.main;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.abe.olasihaber.dao.GenericDao;
import com.abe.olasihaber.model.User;
import com.abe.olasihaber.model.UserCredentials;

public class CreateAdminUser {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Please provide admin username and password");
			return;
		}
		
		String username = args[0];
		String password = args[1];
		
		GenericDao<User> userDao = new GenericDao<User>(User.class);
		User admin = new User(username, "Admin", "Admin", true);
		Long userId = userDao.save(admin).getId();
		System.out.println("User created successfully");
		
		String salt = RandomStringUtils.randomAlphabetic(32);
		String saltedPassword = salt + password;
		UserCredentials uc = new UserCredentials(userId, DigestUtils.sha256Hex(saltedPassword.getBytes()), salt);
		GenericDao<UserCredentials> ucDao = new GenericDao<UserCredentials>(UserCredentials.class);
		ucDao.save(uc);
		
		System.out.println("User credentials saved successfully");
	}
}
