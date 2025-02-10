package kai.cyber.assesment.exception;

/**
 * Custom Exception to be thrown when application fails to save the vulnerabilities into database
 */
public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

