package nxpense.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * A CustomAuthenticationHandler handles successful login request by sending back a response whose content holds
 * the URL that the client is to follow. This mechanism serves as a work-around for clients that don't implicitly
 * follow redirection.
 * 
 * @author kar.hoo.yiu
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	public void onAuthenticationSuccess(HttpServletRequest successfulRequest, 
			HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		
		StringBuilder redirection = new StringBuilder()
			.append(successfulRequest.getContextPath())
			.append("/view/home.html");
		
		String encodedRedirection = response.encodeRedirectURL(redirection.toString());
		
		PrintWriter writer = response.getWriter().append(encodedRedirection);
		writer.flush();
		writer.close();
	}

}
