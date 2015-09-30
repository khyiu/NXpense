package nxpense.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerException extends RuntimeException {

    public ServerException(String msg) {
        super(msg);
    }

    public ServerException(String msg, Exception e) {
        super(msg, e);
    }
}
