package main.exception;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(Throwable cause) {
        super(cause);
    }

    public ManagerLoadException() {
        super();
    }
}
