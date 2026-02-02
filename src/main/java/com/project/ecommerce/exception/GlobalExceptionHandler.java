package com.project.ecommerce.exception;

import com.project.ecommerce.dto.APIErrorResponse;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmailVerificationException.class)
    public ResponseEntity<APIErrorResponse> handleEmailVerificationException(EmailVerificationException ex) {
        Sentry.captureException(ex);
        log.error("Unhandled exception", ex);
        APIErrorResponse apiError = new APIErrorResponse(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<APIErrorResponse> handleGenericException(GenericException ex) {
        Sentry.captureException(ex);
        log.error("Unhandled exception", ex);
        APIErrorResponse apiError = new APIErrorResponse(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GenericIOException.class)
    public ResponseEntity<APIErrorResponse> handleGenericIOException(GenericIOException ex) {
        Sentry.captureException(ex);
        log.error("Unhandled exception", ex);
        APIErrorResponse apiError = new APIErrorResponse(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<APIErrorResponse> handleIOException(IOException ex) {
        Sentry.captureException(ex);
        log.error("Unhandled exception", ex);
        APIErrorResponse apiError =
                new APIErrorResponse("An unexpected error occured: " + ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorResponse> handleRegularException(Exception ex) {
        Sentry.captureException(ex);
        log.error("Unhandled exception", ex);
        APIErrorResponse apiError =
                new APIErrorResponse("An unexpected error occured: " + ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
