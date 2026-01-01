package at.fhj.softsec.baba.exception;

public class StorageAccessException extends ApplicationException {

    public StorageAccessException(String message) {
        super(message);
    }

    public StorageAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
