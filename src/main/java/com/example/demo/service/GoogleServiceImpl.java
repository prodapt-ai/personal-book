package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.execption.GoogleBookExceptions.BookNotFoundException;
import com.example.demo.google.GoogleBookService;
import com.example.demo.pojo.BookDTO;
import com.example.demo.pojo.GoogleBookDetail;

@Service
public class GoogleServiceImpl implements GoogleService {

	private static final Logger log = LoggerFactory.getLogger(GoogleServiceImpl.class);

	private final GoogleBookService googleBookService;
	private final BookPersistenceService bookPersistenceService;

	public GoogleServiceImpl(GoogleBookService googleBookService, BookPersistenceService bookPersistenceService) {
		this.googleBookService = googleBookService;
		this.bookPersistenceService = bookPersistenceService;
	}

	@Override
	public BookDTO fetchAndSaveBook(String googleId) {

		GoogleBookDetail response = googleBookService.fetchBookById(googleId);
		log.info("Google API response {}", response);
		if (response == null) {
			throw new BookNotFoundException(googleId);
		}
		return bookPersistenceService.saveBook(response);
	}
}