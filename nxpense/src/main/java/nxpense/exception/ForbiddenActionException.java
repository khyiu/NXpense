package nxpense.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException(String message) {
        super(message);
    }

    public ForbiddenActionException(String message, Exception e) {
        super(message, e);
    }

    public ForbiddenActionException(Exception e) {
        super(e);
    }
}
