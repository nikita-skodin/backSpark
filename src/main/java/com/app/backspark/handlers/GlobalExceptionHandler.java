package com.app.backspark.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleHttpStatusCodeException(ResponseStatusException ex) {
        logger.warn(ex.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("status", String.valueOf(ex.getStatus().value()));
        body.put("message", ex.getReason() != null ? ex.getReason() : "No reason provided");
        return new ResponseEntity<>(body, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        Map<String, String> body = new HashMap<>();
        body.put("status", "500");
        body.put("message", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(message -> message != null && !message.isEmpty())
                .reduce((msg1, msg2) -> String.join(", ", msg1, msg2))
                .orElse("Validation error");

        Map<String, String> errors = Map.of(
                "status", "400",
                "message", errorMessage
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}
