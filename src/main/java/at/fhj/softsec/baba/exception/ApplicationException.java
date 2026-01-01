package at.fhj.softsec.baba.exception;

public abstract class ApplicationException extends Exception {

    protected ApplicationException(String message) {
        super(message);
    }

    protected ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
