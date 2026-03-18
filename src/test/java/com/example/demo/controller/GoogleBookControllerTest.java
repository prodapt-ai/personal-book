package com.example.demo.controller;

import com.example.demo.execption.GlobalExceptionHandler;
import com.example.demo.google.GoogleBook;
import com.example.demo.pojo.BookDTO;
import com.example.demo.pojo.GoogleBookDetail;
import com.example.demo.service.GoogleService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GoogleBookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GoogleService googleService;

    @InjectMocks
    private GoogleBookController googleBookController;

    private GoogleBook loadJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("effectivejava.json");
        return mapper.readValue(is, GoogleBook.class);
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(googleBookController)
                                 .setControllerAdvice(new GlobalExceptionHandler())
                                 .build();
    }

    @Test
    void testAddBook_Success() throws Exception {
        GoogleBook response = loadJson();
        GoogleBook.Item item = response.items().get(0);

        GoogleBookDetail detail = new GoogleBookDetail(
                item.id(),
                item.id(),
                item.selfLink(),
                item.volumeInfo() != null ? new GoogleBookDetail.VolumeInfo(
                        item.volumeInfo().title(),
                        item.volumeInfo().authors(),
                        item.volumeInfo().publishedDate(),
                        item.volumeInfo().publisher(),
                        item.volumeInfo().pageCount(),
                        item.volumeInfo().printType(),
                        item.volumeInfo().maturityRating(),
                        item.volumeInfo().categories(),
                        item.volumeInfo().language(),
                        item.volumeInfo().previewLink(),
                        item.volumeInfo().infoLink()
                ) : null
        );

        String title = detail.volumeInfo() != null ? detail.volumeInfo().title() : null;
        String author = (detail.volumeInfo() != null &&
                         detail.volumeInfo().authors() != null &&
                         !detail.volumeInfo().authors().isEmpty())
                         ? detail.volumeInfo().authors().get(0)
                         : null;
        int pageCount = detail.volumeInfo() != null ? detail.volumeInfo().pageCount() : 0;

        BookDTO bookDTO = new BookDTO(detail.id(), title, author, pageCount);

        when(googleService.fetchAndSaveBook(detail.id())).thenReturn(bookDTO);

        mockMvc.perform(post("/google/" + detail.id()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(detail.id()))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.pageCount").value(375));

        verify(googleService).fetchAndSaveBook(detail.id());
    }

    @Test
    void testAddBook_BookNotFound() throws Exception {
        doThrow(new RuntimeException("Book not found for Google ID: invalidId"))
                .when(googleService).fetchAndSaveBook("invalidId");

        mockMvc.perform(post("/google/invalidId"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found for Google ID: invalidId"));

        verify(googleService).fetchAndSaveBook("invalidId");
    }

    @Test
    void testAddBook_VolumeInfoEmpty() throws Exception {
        BookDTO bookDTO = new BookDTO("EMPTY01", null, null, 0);

        when(googleService.fetchAndSaveBook("EMPTY01")).thenReturn(bookDTO);

        mockMvc.perform(post("/google/EMPTY01"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("EMPTY01"))
                .andExpect(jsonPath("$.title").isEmpty())
                .andExpect(jsonPath("$.author").isEmpty())
                .andExpect(jsonPath("$.pageCount").value(0));

        verify(googleService).fetchAndSaveBook("EMPTY01");
    }
}