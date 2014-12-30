package service;

import static org.mockito.BDDMockito.*;
import nxpense.domain.User;
import nxpense.repository.UserRepository;
import nxpense.service.UserServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

	private static final String EMAIL_NEW = "new@test.com";
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
		given(passwordEncoder.encode(any(CharSequence.class))).willReturn(PASSWORD);
	}


	@Test
	public void testCreateUser() {	 
		userService.createUser(EMAIL_NEW, PASSWORD.toCharArray(), PASSWORD.toCharArray());
		verify(userRepository, times(1)).save(any(User.class));
	}
}
