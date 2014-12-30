package nxpense.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import nxpense.domain.User;
import nxpense.domain.UserAccount;
import nxpense.exeption.RequestCannotCompleteException;
import nxpense.repository.UserRepository;
import nxpense.service.api.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService{

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	@Qualifier("bcryptEncoder")
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	public void createUser(String email, char[] password, char[] passwordRepeat) {
		LOGGER.info("Creating new user with email={}", email);

		checkPasswordsMatch(password, passwordRepeat);
		checkEmailAlreadyTaken(email);

		User newUser = initializeNewUserWithAccount(email, password);
		userRepository.save(newUser);

		// TODO programmatically authenticate the user
		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void checkEmailAlreadyTaken(String email) {
		User existingUser = userRepository.findByEmail(email);

		if(existingUser != null) {
			LOGGER.error("The provided email ({}) is already taken!", email);
			throw new RequestCannotCompleteException("This email is already taken");
		}
	}

	private void checkPasswordsMatch(char [] password, char [] passwordRepeat) {
		if(!Arrays.equals(password, passwordRepeat)) {
			LOGGER.error("Password confirmation does not match password!");
		}
	}

	private User initializeNewUserWithAccount(String email, char [] password) {
		char [] encodedPassword = passwordEncoder.encode(java.nio.CharBuffer.wrap(password)).toCharArray();
		User user = new User();
		user.setEmail(email);
		user.setPassword(encodedPassword);

		UserAccount userAccount = new UserAccount();
		userAccount.setVerifiedCapital(BigDecimal.ZERO);
		userAccount.setVerifiedDate(new Date());
		user.setUserAccount(userAccount);
		return user;
	}
}
