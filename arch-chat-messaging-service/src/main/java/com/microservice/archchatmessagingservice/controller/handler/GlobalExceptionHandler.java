package com.microservice.archchatmessagingservice.controller.handler;

import com.microservice.archchatmessagingservice.application.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ChatNotFoundException.class,
            FriendshipNotFoundException.class,
            MessageNotFoundException.class
    })
    public ResponseEntity<StandardError> handleNotFoundException(RuntimeException e, HttpServletRequest request) {
        StandardError response = new StandardError(
                LocalDate.now(),
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            AlreadyAreFriendsException.class,
            RequestAlreadyPendingException.class
    })
    public ResponseEntity<StandardError> handleConflictException(RuntimeException e, HttpServletRequest request) {
        StandardError response = new StandardError(
                LocalDate.now(),
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<StandardError> handleForbiddenException(UnauthorizedActionException e, HttpServletRequest request) {
        StandardError response = new StandardError(
                LocalDate.now(),
                HttpStatus.FORBIDDEN.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            InvalidFriendshipStateException.class,
            InvalidMessageStateException.class,
            InvalidSelfActionException.class,
            UserBlockedException.class
    })
    public ResponseEntity<StandardError> handleBadRequestException(RuntimeException e, HttpServletRequest request) {
        StandardError response = new StandardError(
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {

        List<String> errorsList = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        String errorMessage = "Falha na validação: " + String.join(", ", errorsList);

        StandardError response = new StandardError(
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}