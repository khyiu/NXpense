package nxpense.service;


import nxpense.domain.User;
import nxpense.security.CustomUserDetails;
import org.junit.Before;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Collections;

public class AbstractServiceTest {

    protected static final String USER_EMAIL = "test@test.com";
    protected static final String USER_PASSWORD = "secret";
    protected User mockUser;

    @Before
    public void initAuthenticationMock() throws NoSuchFieldException, IllegalAccessException {
        mockUser = new User();
        mockUser.setEmail(USER_EMAIL);
        mockUser.setPassword(USER_PASSWORD.toCharArray());

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockUser, Integer.parseInt("1"));

        UserDetails userDetails = new CustomUserDetails(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
