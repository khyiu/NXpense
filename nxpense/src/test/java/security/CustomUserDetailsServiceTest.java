package security;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import nxpense.domain.User;
import nxpense.exception.BadCredentialsException;
import nxpense.repository.UserRepository;
import nxpense.security.CustomUserDetailsService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {
    
    private static final String EXISTING_EMAIL = "username1@test.com";
    private static final String UNEXISTING_EMAIL = "username2@test.com";
    
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    	
    @Mock
    private UserRepository userRepository;
    
    @Before
    public void configureMocks() {
	User user = new User();
	user.setEmail(EXISTING_EMAIL);
	user.setPassword(new char[]{});
	
	given(userRepository.findByEmail(EXISTING_EMAIL)).willReturn(user);
	given(userRepository.findByEmail(UNEXISTING_EMAIL)).willReturn(null);
    }
    
    @Test
    public void testLoadUserByUsername() {
	UserDetails userDetails = customUserDetailsService.loadUserByUsername(EXISTING_EMAIL);
	assertThat(userDetails.getUsername()).isEqualTo(EXISTING_EMAIL);
    }
    
    @Test(expected = BadCredentialsException.class)
    public void testLoadUserByUsername_UnexistingEmail() {
	customUserDetailsService.loadUserByUsername(UNEXISTING_EMAIL);
    }
}
