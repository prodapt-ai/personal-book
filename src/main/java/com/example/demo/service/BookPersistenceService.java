package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.db.Book;
import com.example.demo.mapper.BookMapper;
import com.example.demo.pojo.BookDTO;
import com.example.demo.pojo.GoogleBookDetail;
import com.example.demo.repository.GoogleRepository;

import jakarta.transaction.Transactional;

@Service
public class BookPersistenceService {

	private final GoogleRepository googleRepository;
	private final BookMapper mapper;

	public BookPersistenceService(GoogleRepository googleRepository, BookMapper mapper) {
		this.googleRepository = googleRepository;
		this.mapper = mapper;
	}

	@Transactional
	public BookDTO saveBook(GoogleBookDetail googleBookDetail) {
		Book book = mapper.toEntity(googleBookDetail);
		book.setPageCount(googleBookDetail.volumeInfo().pageCount());
		Book savedBook = googleRepository.save(book);

		return mapper.toDTO(savedBook);
	}
}
