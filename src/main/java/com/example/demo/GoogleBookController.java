package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.pojo.BookDTO;
import com.example.demo.service.GoogleService;

@RestController
@RequestMapping("/google")
public class GoogleBookController {

	private final GoogleService googleService;

	public GoogleBookController(GoogleService googleService) {
		this.googleService = googleService;
	}

	@GetMapping("/test")
	public String test() {
		return "Working";
	}

	@PostMapping("/{googleId}")
	public ResponseEntity<BookDTO> addBook(@PathVariable String googleId) {
		BookDTO saved = googleService.fetchAndSaveBook(googleId);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
}