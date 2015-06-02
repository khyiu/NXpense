package nxpense.service;

import nxpense.domain.User;
import nxpense.exception.RequestCannotCompleteException;
import nxpense.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String EMAIL_NEW = "new@test.com";
    private static final String EMAIL_EXISTING = "existing@test.com";
    private static final String PASSWORD = "gue$$it";

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Before
    public void configureMocks() {
	given(userRepository.findByEmail(EMAIL_NEW)).willReturn(null);
	given(userRepository.findByEmail(EMAIL_EXISTING)).willReturn(new User());
	
	given(passwordEncoder.encode(any(CharSequence.class))).willReturn(PASSWORD);
    }


    @Test
    public void testCreateUser() {	 
	userService.createUser(EMAIL_NEW, PASSWORD.toCharArray(), PASSWORD.toCharArray());
	verify(userRepository, times(1)).save(any(User.class));
    }

    @Test(expected = RequestCannotCompleteException.class)
    public void testCreateUser_ExistingUser() {
	userService.createUser(EMAIL_EXISTING, PASSWORD.toCharArray(), PASSWORD.toCharArray());
    }
    
    @Test(expected = RequestCannotCompleteException.class)
    public void testCreateUser_WrongConfirmation() {
	userService.createUser(EMAIL_NEW, PASSWORD.toCharArray(), "wrong confirmation".toCharArray());
    }
}
