package nxpense.helper;

import nxpense.domain.User;
import nxpense.exception.UnauthenticatedException;
import nxpense.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityPrincipalHelper {

    private SecurityPrincipalHelper() {

    }

    public static User getCurrentUser() {
        User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (currentUser == null) {
            throw new UnauthenticatedException("User does not seem to be authenticated!");
        }

        return currentUser;
    }
}
