package com.example.demo.google;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import jakarta.transaction.Transactional;

@Service
public class GoogleBookService {

	private RestClient restClient;

	public GoogleBookService(RestClient restClient, BookRepository bookRepository) {
		this.restClient = restClient;
	}

	public GoogleBook searchBooks(String query, Integer maxResults, Integer startIndex) {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder.path("/volumes").queryParam("q", query)
						.queryParam("maxResults", maxResults != null ? maxResults : 10)
						.queryParam("startIndex", startIndex != null ? startIndex : 0).build())
				.retrieve().body(GoogleBook.class);
	}

}
