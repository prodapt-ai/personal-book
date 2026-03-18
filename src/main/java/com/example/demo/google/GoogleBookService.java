package com.example.demo.google;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.pojo.GoogleBookDetail;

@Service
public class GoogleBookService {

	private final RestClient restClient;

	public GoogleBookService(RestClient restClient) {
		this.restClient = restClient;
	}

	// Search API
	public GoogleBook searchBooks(String query, Integer maxResults, Integer startIndex) {

		return restClient.get()
				.uri(uriBuilder -> uriBuilder.path("/volumes").queryParam("q", query)
						.queryParam("maxResults", maxResults != null ? maxResults : 10)
						.queryParam("startIndex", startIndex != null ? startIndex : 0).build())
				.retrieve().body(GoogleBook.class);
	}

	// Fetch by Volume ID
	public GoogleBookDetail fetchBookById(String googleId) {

		return restClient.get().uri("/volumes/{id}", googleId).retrieve().body(GoogleBookDetail.class);
	}
}