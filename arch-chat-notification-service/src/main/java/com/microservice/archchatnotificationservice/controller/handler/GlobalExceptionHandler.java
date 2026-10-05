package com.microservice.archchatnotificationservice.controller.handler;

import com.microservice.archchatnotificationservice.application.exceptions.ForbiddenActionException;
import com.microservice.archchatnotificationservice.application.exceptions.NotificationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<StandardError> handleNotFound(NotificationNotFoundException e, HttpServletRequest request) {
        StandardError error = new StandardError(
                LocalDate.now(),
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<StandardError> handleForbidden(ForbiddenActionException e, HttpServletRequest request) {
        StandardError error = new StandardError(
                LocalDate.now(),
                HttpStatus.FORBIDDEN.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
