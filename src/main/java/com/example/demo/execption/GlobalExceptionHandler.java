package com.example.demo.execption;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.example.demo.execption.GoogleBookExceptions.BookNotFoundException;
import com.example.demo.pojo.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Common method to build ErrorResponse
    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }

    // Handle specific BookNotFoundException
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(
            BookNotFoundException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                buildErrorResponse(status, ex.getMessage(), request.getRequestURI()),
                status
        );
    }

    // Handle 4xx errors from external APIs
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleClientError(
            HttpClientErrorException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        return new ResponseEntity<>(
                buildErrorResponse(status, ex.getMessage(), request.getRequestURI()),
                status
        );
    }

    // Handle 5xx errors from external APIs
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleServerError(
            HttpServerErrorException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        return new ResponseEntity<>(
                buildErrorResponse(status, ex.getMessage(), request.getRequestURI()),
                status
        );
    }

    // Handle generic RuntimeException (keep it, but not masking specific ones)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                buildErrorResponse(status, ex.getMessage(), request.getRequestURI()),
                status
        );
    }

    // Handle generic Exception (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(
                buildErrorResponse(status, ex.getMessage(), request.getRequestURI()),
                status
        );
    }
}