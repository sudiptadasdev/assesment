package kai.cyber.assesment.exception;

/**
 * Custom exception to be thrown when our appication fails to connect Github api in two attempts
 */
public class GitHubConnectionException extends RuntimeException {
    public GitHubConnectionException(String message) {
        super(message);
    }

    public GitHubConnectionException(String message, Throwable cause) {
        super(message, cause);
    }


}

