package nxpense.service;


import nxpense.domain.User;
import nxpense.security.CustomUserDetails;
import org.junit.Before;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

public class AbstractServiceTest {

    protected static final String USER_EMAIL = "test@test.com";
    protected static final String USER_PASSWORD = "secret";
    protected User mockUser;

    @Before
    public void initAuthenticationMock() {
        mockUser = new User();
        mockUser.setEmail(USER_EMAIL);
        mockUser.setPassword(USER_PASSWORD.toCharArray());

        UserDetails userDetails = new CustomUserDetails(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
