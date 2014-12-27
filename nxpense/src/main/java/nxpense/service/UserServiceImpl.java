package nxpense.service;

import java.math.BigDecimal;
import java.util.Date;

import nxpense.domain.User;
import nxpense.domain.UserAccount;
import nxpense.exeption.RequestCannotCompleteException;
import nxpense.repository.UserRepository;
import nxpense.service.api.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    public void createUser(String email, char[] password, char[] passwordRepeat) {
	LOGGER.info("Creating new user with email={}", email);

	User existingUser = userRepository.findByEmail(email);

	if(existingUser != null) {
	    LOGGER.error("The provided email ({}) is already taken!", email);
	    throw new RequestCannotCompleteException("This email is already taken");
	}

	User user = new User();
	user.setEmail(email);
	user.setPassword(password);

	UserAccount userAccount = new UserAccount();
	userAccount.setVerifiedCapital(BigDecimal.ZERO);
	userAccount.setVerifiedDate(new Date());
	user.setUserAccount(userAccount);

	userRepository.save(user);		
    }

}
