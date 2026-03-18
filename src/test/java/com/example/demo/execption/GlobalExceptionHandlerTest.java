package com.example.demo.execption;

import com.example.demo.pojo.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Book not found");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/google/123");

        ResponseEntity<ErrorResponse> response =
                handler.handleRuntimeException(ex, request);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Book not found", response.getBody().getMessage());
        assertEquals("NOT_FOUND", response.getBody().getError());
        assertEquals("/api/google/123", response.getBody().getPath());
    }

    @Test
    void testHandleException() {
        Exception ex = new Exception("Something went wrong");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/google/123");

        ResponseEntity<ErrorResponse> response =
                handler.handleException(ex, request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Something went wrong", response.getBody().getMessage());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
        assertEquals("/api/google/123", response.getBody().getPath());
    }
}