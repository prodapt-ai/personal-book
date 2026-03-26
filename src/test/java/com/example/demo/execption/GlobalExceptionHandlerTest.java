package com.example.demo.execption;

import com.example.demo.execption.GoogleBookExceptions.BookNotFoundException;
import com.example.demo.pojo.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    // RuntimeException - 400 BAD_REQUEST
    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Book not found");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/google/123");

        ResponseEntity<ErrorResponse> response =
                handler.handleRuntimeException(ex, request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Book not found", response.getBody().getMessage());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("/api/google/123", response.getBody().getPath());
    }

    // Generic Exception - 500 INTERNAL_SERVER_ERROR
    @Test
    void testHandleException() {
        Exception ex = new Exception("Something went wrong");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/google/123");

        ResponseEntity<ErrorResponse> response =
                handler.handleException(ex, request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Something went wrong", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("/api/google/123", response.getBody().getPath());
    }

    // BookNotFoundException - 404 NOT_FOUND
    @Test
    void testHandleBookNotFound() {
        BookNotFoundException ex = new BookNotFoundException("123");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/google/123");

        ResponseEntity<ErrorResponse> response =
                handler.handleBookNotFound(ex, request);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Book not found for Google ID: 123", response.getBody().getMessage());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("/api/google/123", response.getBody().getPath());
    }

    // 4xx External API Error
    @Test
    void testHandleClientError() {
        HttpClientErrorException ex =
                new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/google/400");

        ResponseEntity<ErrorResponse> response =
                handler.handleClientError(ex, request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("/api/google/400", response.getBody().getPath());
    }

    //  5xx External API Error
    @Test
    void testHandleServerError() {
        HttpServerErrorException ex =
                new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/google/500");

        ResponseEntity<ErrorResponse> response =
                handler.handleServerError(ex, request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("/api/google/500", response.getBody().getPath());
    }
}