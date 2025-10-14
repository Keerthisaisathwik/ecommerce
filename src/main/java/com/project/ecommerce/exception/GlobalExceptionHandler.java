package com.project.ecommerce.exception;

import com.project.ecommerce.dto.APIErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorResponse> handleRegularException(Exception ex) {
        APIErrorResponse apiError = new APIErrorResponse("An unexpected error occured: " + ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<APIErrorResponse> handleIOException(IOException ex) {
        APIErrorResponse apiError = new APIErrorResponse("An unexpected error occured: " + ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GenericIOException.class)
    public ResponseEntity<APIErrorResponse> handleGenericIOException(GenericIOException ex) {
        APIErrorResponse apiError = new APIErrorResponse(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<APIErrorResponse> handleGenericException(GenericException ex) {
        APIErrorResponse apiError = new APIErrorResponse(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
