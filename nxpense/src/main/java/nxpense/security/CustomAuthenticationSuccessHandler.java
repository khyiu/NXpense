package nxpense.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A CustomAuthenticationSuccessHandler handles successful login requests by sending back a response whose content holds
 * the URL that the client is to follow. This mechanism serves as a work-around for clients that don't implicitly
 * follow redirection.
 * 
 * @author kar.hoo.yiu
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class); 

	public void onAuthenticationSuccess(HttpServletRequest successfulRequest, 
			HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		LOGGER.info("User [{}] logged in with success", authentication.getName());
		
		StringBuilder redirection = new StringBuilder()
			.append(successfulRequest.getContextPath())
			.append("/view/home.html");
		
		String encodedRedirection = response.encodeRedirectURL(redirection.toString());
		
		PrintWriter writer = response.getWriter().append(encodedRedirection);
		writer.flush();
		writer.close();
	}

}
