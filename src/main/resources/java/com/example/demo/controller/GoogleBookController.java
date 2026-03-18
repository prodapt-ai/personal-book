package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.db.Book;
import com.example.demo.service.GoogleService;

@RestController	
@RequestMapping("/api/google")
public class GoogleBookController {

	@Autowired
	GoogleService googleService;

	@PostMapping("/{googleId}")
	public ResponseEntity<Book> addBook(@PathVariable String googleId) {
		Book saved = googleService.fetchByVolumeIdAndSave(googleId);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
}
