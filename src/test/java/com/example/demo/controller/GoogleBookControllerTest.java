package com.example.demo.controller;

import com.example.demo.execption.GlobalExceptionHandler;
import com.example.demo.pojo.BookDTO;
import com.example.demo.pojo.GoogleBookDetail;
import com.example.demo.service.GoogleService;
import com.example.demo.util.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GoogleBookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GoogleService googleService;

    @InjectMocks
    private GoogleBookController googleBookController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(googleBookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // Parameterized data (single + multiple authors)
    static Stream<List<String>> authorProvider() {
        return Stream.of(
                List.of("Joshua Bloch"),
                List.of("Author1", "Author2")
        );
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void testAddBook_Success(List<String> authors) throws Exception {

        GoogleBookDetail detail = TestUtils.buildGoogleBookDetail(
                "123",
                "Effective Java",
                authors,
                375
        );

        String author = authors != null && !authors.isEmpty() ? authors.get(0) : null;

        BookDTO bookDTO = new BookDTO(detail.id(), "Effective Java", author, 375);

        when(googleService.fetchAndSaveBook(detail.id())).thenReturn(bookDTO);

        mockMvc.perform(post("/google/" + detail.id()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value(detail.id()))
                .andExpect(jsonPath("$.bookTitle").value("Effective Java"))
                .andExpect(jsonPath("$.bookAuthor").value(author))
                .andExpect(jsonPath("$.pageCount").value(375));

        verify(googleService).fetchAndSaveBook(detail.id());
    }

    // Book Not Found (specific exception)
    @Test
    void testAddBook_BookNotFound() throws Exception {

        doThrow(new RuntimeException("Book not found for Google ID: invalidId"))
                .when(googleService).fetchAndSaveBook("invalidId");

        mockMvc.perform(post("/google/invalidId"))
                .andExpect(status().isBadRequest()); // based on your handler

        verify(googleService).fetchAndSaveBook("invalidId");
    }

    // 4xx External API error
    @Test
    void testAddBook_ClientError() throws Exception {

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
                .when(googleService).fetchAndSaveBook("400");

        mockMvc.perform(post("/google/400"))
                .andExpect(status().isBadRequest());

        verify(googleService).fetchAndSaveBook("400");
    }

    // 5xx External API error
    @Test
    void testAddBook_ServerError() throws Exception {

        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(googleService).fetchAndSaveBook("500");

        mockMvc.perform(post("/google/500"))
                .andExpect(status().isInternalServerError());

        verify(googleService).fetchAndSaveBook("500");
    }

    // Empty volume info
    @Test
    void testAddBook_VolumeInfoEmpty() throws Exception {

        BookDTO bookDTO = new BookDTO("EMPTY01", null, null, 0);

        when(googleService.fetchAndSaveBook("EMPTY01")).thenReturn(bookDTO);

        mockMvc.perform(post("/google/EMPTY01"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value("EMPTY01"))
                .andExpect(jsonPath("$.bookTitle").isEmpty())
                .andExpect(jsonPath("$.bookAuthor").isEmpty())
                .andExpect(jsonPath("$.pageCount").value(0));

        verify(googleService).fetchAndSaveBook("EMPTY01");
    }
}