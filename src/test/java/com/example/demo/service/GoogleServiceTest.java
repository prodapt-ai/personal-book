package com.example.demo.service;

import com.example.demo.execption.GoogleBookExceptions.BookNotFoundException;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import com.example.demo.pojo.BookDTO;
import com.example.demo.pojo.GoogleBookDetail;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;

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

	private GoogleBook loadJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		InputStream is = getClass().getClassLoader().getResourceAsStream("effectivejava.json");
		return mapper.readValue(is, GoogleBook.class);
	}

	@Test
	void testFetchAndSaveBook_HappyPath() throws Exception {
		GoogleBook response = loadJson();

		assertNotNull(response.items());
		assertFalse(response.items().isEmpty());

		GoogleBook.Item item = response.items().get(0);
		GoogleBookDetail detail = new GoogleBookDetail(item.id(), item.id(),
				item.selfLink(),
				item.volumeInfo() != null ? new GoogleBookDetail.VolumeInfo(item.volumeInfo().title(),
						item.volumeInfo().authors(), item.volumeInfo().publishedDate(), item.volumeInfo().publisher(),
						item.volumeInfo().pageCount(), item.volumeInfo().printType(),
						item.volumeInfo().maturityRating(), item.volumeInfo().categories(),
						item.volumeInfo().language(), item.volumeInfo().previewLink(), item.volumeInfo().infoLink())
						: null);

		when(googleBookService.fetchBookById(detail.id())).thenReturn(detail);

		BookDTO dto = new BookDTO(detail.id(), detail.volumeInfo().title(), detail.volumeInfo().authors().get(0),
				detail.volumeInfo().pageCount());
		when(bookPersistenceService.saveBook(detail)).thenReturn(dto);
		BookDTO result = googleService.fetchAndSaveBook(detail.id());
		assertEquals(detail.id(), result.getId());
		assertEquals("Effective Java", result.getTitle());
		assertEquals("Joshua Bloch", result.getAuthor());
		assertEquals(375, result.getPageCount());
	}


	@Test
	void testFetchAndSaveBook_BookNotFound() {
		when(googleBookService.fetchBookById("invalidId")).thenReturn(null);
		BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
			googleService.fetchAndSaveBook("invalidId");
		});

		assertEquals("Book not found for Google ID: invalidId", exception.getMessage());
	}

	@Test
	void testFetchAndSaveBook_VolumeInfoEmpty() {
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