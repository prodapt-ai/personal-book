package com.example.demo.service;

import com.example.demo.execption.GoogleBookExceptions.BookNotFoundException;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import com.example.demo.pojo.BookDTO;
import com.example.demo.pojo.GoogleBookDetail;
import com.example.demo.util.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleServiceTest {

    @Mock
    private GoogleBookService googleBookService;

    @Mock
    private BookPersistenceService bookPersistenceService;

    @InjectMocks
    private GoogleServiceImpl googleService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        googleService = new GoogleServiceImpl(googleBookService, bookPersistenceService);
    }

    // Happy Path + ArgumentCaptor
    @Test
    void testFetchAndSaveBook_HappyPath() throws Exception {

        GoogleBook response = TestUtils.loadJson("effectivejava.json");
        GoogleBook.Item item = response.items().get(0);

        GoogleBookDetail detail = TestUtils.buildGoogleBookDetail(
                item.id(),
                item.volumeInfo().title(),
                item.volumeInfo().authors(),
                item.volumeInfo().pageCount()
        );

        when(googleBookService.fetchBookById(detail.id())).thenReturn(detail);

        BookDTO dto = new BookDTO(
                detail.id(),
                detail.volumeInfo().title(),
                detail.volumeInfo().authors().get(0),
                detail.volumeInfo().pageCount()
        );

        when(bookPersistenceService.saveBook(any())).thenReturn(dto);

        BookDTO result = googleService.fetchAndSaveBook(detail.id());

        // ArgumentCaptor
        ArgumentCaptor<GoogleBookDetail> captor = ArgumentCaptor.forClass(GoogleBookDetail.class);
        verify(bookPersistenceService).saveBook(captor.capture());

        GoogleBookDetail captured = captor.getValue();
        assertEquals(detail.id(), captured.id());

        // Result assertions
        assertEquals("Effective Java", result.getTitle());
        assertEquals("Joshua Bloch", result.getAuthor());
        assertEquals(375, result.getPageCount());
    }

    // Multiple Authors scenario
    @Test
    void testFetchAndSaveBook_MultipleAuthors() {

        GoogleBookDetail detail = TestUtils.buildGoogleBookDetail(
                "MULTI01",
                "Test Book",
                List.of("Author1", "Author2"),
                200
        );

        when(googleBookService.fetchBookById("MULTI01")).thenReturn(detail);

        BookDTO dto = new BookDTO("MULTI01", "Test Book", "Author1", 200);
        when(bookPersistenceService.saveBook(detail)).thenReturn(dto);

        BookDTO result = googleService.fetchAndSaveBook("MULTI01");

        assertEquals("Author1", result.getAuthor()); // first author
    }

    // Existing Book ID scenario
    @Test
    void testFetchAndSaveBook_ExistingBook() {

        GoogleBookDetail detail = TestUtils.buildGoogleBookDetail(
                "EXIST01",
                "Existing Book",
                List.of("Author"),
                150
        );

        when(googleBookService.fetchBookById("EXIST01")).thenReturn(detail);

        // Simulate existing record (same ID returned)
        BookDTO dto = new BookDTO("EXIST01", "Existing Book", "Author", 150);
        when(bookPersistenceService.saveBook(detail)).thenReturn(dto);

        BookDTO result = googleService.fetchAndSaveBook("EXIST01");

        assertNotNull(result);
        assertEquals("EXIST01", result.getId());
    }

    // Book Not Found
    @Test
    void testFetchAndSaveBook_BookNotFound() {

        when(googleBookService.fetchBookById("invalidId")).thenReturn(null);

        BookNotFoundException ex = assertThrows(BookNotFoundException.class,
                () -> googleService.fetchAndSaveBook("invalidId"));

        assertEquals("Book not found for Google ID: invalidId", ex.getMessage());
    }

    // Exception from external API (4xx/5xx simulation)
    @Test
    void testFetchAndSaveBook_ExternalServiceException() {

        when(googleBookService.fetchBookById("ERR01"))
                .thenThrow(new RuntimeException("External API failure"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> googleService.fetchAndSaveBook("ERR01"));

        assertEquals("External API failure", ex.getMessage());
    }

    // VolumeInfo null (edge case)
    @Test
    void testFetchAndSaveBook_VolumeInfoNull() {

        GoogleBookDetail detail = new GoogleBookDetail("kind", "EMPTY01", null, null);

        when(googleBookService.fetchBookById("EMPTY01")).thenReturn(detail);

        BookDTO dto = new BookDTO("EMPTY01", null, null, 0);
        when(bookPersistenceService.saveBook(detail)).thenReturn(dto);

        BookDTO result = googleService.fetchAndSaveBook("EMPTY01");

        assertNotNull(result);
        assertNull(result.getTitle());
        assertNull(result.getAuthor());
        assertEquals(0, result.getPageCount());
    }
}