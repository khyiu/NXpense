 package nxpense.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class RequestCannotCompleteException extends RuntimeException {

    private static final long serialVersionUID = 7209170909770768104L;

    public RequestCannotCompleteException(String message) {
        super(message);
    }

    public RequestCannotCompleteException(String message, Exception e) {
        super(message, e);
    }

    public RequestCannotCompleteException(Exception e) {
        super(e);
    }
}
