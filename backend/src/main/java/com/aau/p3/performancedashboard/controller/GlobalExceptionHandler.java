package com.aau.p3.performancedashboard.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aau.p3.performancedashboard.exceptions.IntegrationNotFoundException;
import com.aau.p3.performancedashboard.payload.response.CustomResponse;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;

import io.micrometer.core.ipc.http.HttpSender.Response;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = { Exception.class })
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        logger.debug("An exception occurred and handled by GlobalExceptionHandler", ex);
        if (ex instanceof IntegrationNotFoundException) {
            return handleIntegrationNotFoundException((IntegrationNotFoundException) ex);
        } else if (ex instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValidException((MethodArgumentNotValidException) ex);
        } else if (ex instanceof AuthenticationException) {
            return handleAuthenticationException((AuthenticationException) ex);
        } else if(ex instanceof AccessDeniedException) {
            return handleAccessDeniedException((AccessDeniedException) ex);
        } else {
            return handleGenericException(ex);
        }
    }

    protected ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(new ErrorResponse("Internal Server Error", status.toString(), ex.getMessage()));
    }

    protected ResponseEntity<ErrorResponse> handleIntegrationNotFoundException(IntegrationNotFoundException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(new ErrorResponse("Integration Not Found", status.toString(), ex.getMessage()));
    }

    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Extract error messages from the exception
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                    } else {
                        return error.getDefaultMessage();
                    }
                })
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse("Validation Error", status.toString(), errors);
        return ResponseEntity.status(status).body(response);
    }

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse response = new ErrorResponse("Error authenticating.", status.toString() , ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse response = new ErrorResponse("Access denied.", status.toString() , ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }
}
