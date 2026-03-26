package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.execption.GoogleBookExceptions.BookNotFoundException;
import com.example.demo.google.GoogleBookService;
import com.example.demo.pojo.BookDTO;
import com.example.demo.pojo.GoogleBookDetail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GoogleServiceImpl implements GoogleService {

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