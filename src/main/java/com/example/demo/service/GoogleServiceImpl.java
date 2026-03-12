package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.example.demo.db.Book;
import com.example.demo.execption.GoogleBookExceptions;
import com.example.demo.pojo.GoogleBookDetail;
import com.example.demo.repository.GoogleRepository;

import jakarta.transaction.Transactional;

@Service
public class GoogleServiceImpl implements GoogleService {

	@Autowired
	GoogleRepository googleRepository;

	private static final Logger log = LoggerFactory.getLogger(GoogleServiceImpl.class);

	private RestClient restClient;

	public GoogleServiceImpl(RestClient restClient) {
		this.restClient = restClient;
	}

	@Transactional
	public Book fetchByVolumeIdAndSave(String googleId) {
		GoogleBookDetail googleBookDetail = fetchFromAPI(googleId);
		if (googleBookDetail == null || googleBookDetail.volumeInfo() == null) {
			log.warn("null or empty items[] - googleId={}", googleId);
			throw new GoogleBookExceptions.InvalidBookDataException(googleId);
		}
		Book saved = googleRepository.save(mapToEntity(googleBookDetail));
		log.info("Book persisted - id={} title={}", saved.getId(), saved.getTitle());
		return saved;
	}

	private GoogleBookDetail fetchFromAPI(String googleId) {
		try {
			GoogleBookDetail response = restClient.get()
					.uri(uriBuilder -> uriBuilder.path("/volumes/{Id}").build(googleId)).retrieve()
					.body(GoogleBookDetail.class);
			log.info("response:{}", response);
			return response;
		} catch (HttpClientErrorException.NotFound ex) {
			log.warn("Google Books API 404 - googleID={}", googleId);
			throw new GoogleBookExceptions.BookNotFoundException(googleId);
		} catch (HttpClientErrorException ex) {
			log.error("Google Books API error - googleId={} status={}", googleId, ex.getStatusCode());
			throw new GoogleBookExceptions.InvalidBookDataException(googleId);
		}

	}

	private Book mapToEntity(GoogleBookDetail googleBook) {
		String firstAuther = (googleBook.volumeInfo().authors() != null && !googleBook.volumeInfo().authors().isEmpty())
				? googleBook.volumeInfo().authors().get(0)
				: null;
		return new Book(googleBook.id(), googleBook.volumeInfo().title(), firstAuther,
				googleBook.volumeInfo().pageCount());
	}

}
