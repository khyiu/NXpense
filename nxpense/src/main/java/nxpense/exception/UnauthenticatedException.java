package nxpense.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Client must be authenticated to complete action")
public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException(String message) {
        super(message);
    }
}
