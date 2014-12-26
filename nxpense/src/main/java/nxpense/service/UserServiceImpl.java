package nxpense.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nxpense.domain.User;
import nxpense.repository.UserRepository;
import nxpense.service.api.UserService;

@Service("userService")
public class UserServiceImpl implements UserService{

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserRepository userRepository;
	
	public void createUser(String email, char[] password, char[] passwordRepeat) {
		
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		userRepository.save(user);
		System.out.println(">>> creating new user");
	}

}
