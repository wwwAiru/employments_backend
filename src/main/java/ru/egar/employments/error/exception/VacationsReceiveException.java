package ru.egar.employments.error.exception;

import ru.egartech.sdk.exception.ApplicationException;

public class VacationsReceiveException extends ApplicationException {

    public VacationsReceiveException() {
    }

    public VacationsReceiveException(String message) {
        super(message);
    }

    public VacationsReceiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public VacationsReceiveException(Throwable cause) {
        super(cause);
    }
}
