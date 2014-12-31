package nxpense.security;

import nxpense.domain.User;
import nxpense.exeption.BadCredentialsException;
import nxpense.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	User user = userRepository.findByEmail(username);

	if(user == null) {
	    LOGGER.warn("No user found in DB with email={}", username);
	    throw new BadCredentialsException("No user found with provided email");
	}

	CustomUserDetails userDetails = new CustomUserDetails(user.getEmail(), new String(user.getPassword()));
	return userDetails;
    }

}
