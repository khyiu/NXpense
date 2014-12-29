package nxpense.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * A CustomAuthenticationFailureHandler handles failed login requests by sending back a response with a specific header
 * based on which, the client is able to display a relevant error message without having to follow any redirection.
 * 
 * @author kar.hoo.yiu
 */
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

	public void onAuthenticationFailure(HttpServletRequest failedRequest, HttpServletResponse response, 
			AuthenticationException authenticationException) throws IOException, ServletException {
		LOGGER.info("Login attempt failed", authenticationException);
		
		response.setHeader("nxpense-login-error", "Bad credentials");
	}

}
