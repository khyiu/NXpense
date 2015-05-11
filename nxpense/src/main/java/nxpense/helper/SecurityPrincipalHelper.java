package nxpense.helper;

import nxpense.domain.User;
import nxpense.exception.UnauthenticatedException;
import nxpense.repository.UserRepository;
import nxpense.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityPrincipalHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityPrincipalHelper.class);

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (currentUser == null) {
            throw new UnauthenticatedException("User does not seem to be authenticated!");
        }

        String currentUserEmail = currentUser.getEmail();
        currentUser = userRepository.findOne(currentUser.getId());

        if(currentUser == null) {
            LOGGER.warn("User with email [{}] seems is authenticated through the application but couldn't be found in database!", currentUserEmail);
            throw new UnauthenticatedException("User not found in database!");
        }

        return currentUser;
    }
}
