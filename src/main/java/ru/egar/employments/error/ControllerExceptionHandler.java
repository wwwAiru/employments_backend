package ru.egar.employments.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import ru.egar.employments.error.exception.VacationsCantReceivedException;
import ru.egartech.sdk.exception.dto.ApiErrorDto;
import ru.egartech.sdk.exception.handler.AbstractRestExceptionHandler;
import ru.egartech.sdk.util.MessageSourceUtils;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ControllerExceptionHandler extends AbstractRestExceptionHandler {
    private final MessageSourceUtils messageSource;

    public ControllerExceptionHandler(MessageSourceUtils messageSourceUtils) {
        super(messageSourceUtils);
        this.messageSource = messageSourceUtils;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiErrorDto handleMissedException(RuntimeException exception, WebRequest webRequest) {
        exception.printStackTrace();
        return buildMessage(messageSource, exception, webRequest, "unknownError", exception.getLocalizedMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiErrorDto handleResourceAccessException(RuntimeException exception, WebRequest webRequest) {
        exception.printStackTrace();
        return buildMessage(messageSource, exception, webRequest, "vacationServiceError", exception.getLocalizedMessage());
    }

    @ExceptionHandler(VacationsCantReceivedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiErrorDto handleNotAvailableException(VacationsCantReceivedException exception, WebRequest webRequest) {
        exception.printStackTrace();
        return buildMessage(messageSource, exception, webRequest, "vacationsServiceError", exception.getLocalizedMessage());
    }

}
