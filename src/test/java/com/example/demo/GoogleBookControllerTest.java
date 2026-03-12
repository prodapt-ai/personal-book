package com.example.demo;

import com.example.demo.Controller.GoogleBookController;
import com.example.demo.db.Book;
import com.example.demo.execption.GlobalExceptionHandler;
import com.example.demo.service.GoogleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
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

    @Test
    void testAddBook_Success() throws Exception {
        Book book = new Book("BREwDwAAQBAJ", "Effective Java", "Joshua Bloch");
        book.setPageCount(264);

        when(googleService.fetchByVolumeIdAndSave("BREwDwAAQBAJ"))
                .thenReturn(book);

        mockMvc.perform(post("/api/google/BREwDwAAQBAJ"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("BREwDwAAQBAJ"))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.pageCount").value(264));
    }

    @Test
    void testAddBook_BookNotFound() throws Exception {
        doThrow(new RuntimeException("Book not found"))
                .when(googleService)
                .fetchByVolumeIdAndSave("invalidId");

        mockMvc.perform(post("/api/google/invalidId"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }
}