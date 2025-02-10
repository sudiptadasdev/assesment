package kai.cyber.assesment.exception;

public class GitHubConnectionException extends RuntimeException {
    public GitHubConnectionException(String message) {
        super(message);
    }

    public GitHubConnectionException(String message, Throwable cause) {
        super(message, cause);
    }


}

