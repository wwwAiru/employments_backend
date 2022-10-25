package ru.egar.employments.error.exception;

import ru.egartech.sdk.exception.ApplicationException;

public class VacationsCantReceivedException extends ApplicationException {

    public VacationsCantReceivedException() {
    }

    public VacationsCantReceivedException(String message) {
        super(message);
    }

    public VacationsCantReceivedException(String message, Throwable cause) {
        super(message, cause);
    }

    public VacationsCantReceivedException(Throwable cause) {
        super(cause);
    }
}
