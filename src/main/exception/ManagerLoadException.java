package main.exception;

import main.manager.Managers;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(Throwable cause) {
        super(cause);
    }
    public ManagerLoadException() {
        super();
    }
}
