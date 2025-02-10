package kai.cyber.assesment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Exception to be thrown when there is no matching vulnerability with given filter
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class VulneribilityNotFoundException extends RuntimeException {

    public VulneribilityNotFoundException(String message) {
        super(message);
    }
}
