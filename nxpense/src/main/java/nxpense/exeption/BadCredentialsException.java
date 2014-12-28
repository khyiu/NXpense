package nxpense.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Wrong email/password")
public class BadCredentialsException extends RuntimeException {
	
	private static final long serialVersionUID = 2624061857006180274L;

	public BadCredentialsException(String cause) {
		super(cause);
	}
}
