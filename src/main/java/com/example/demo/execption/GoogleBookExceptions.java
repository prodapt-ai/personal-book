package com.example.demo.execption;

public class GoogleBookExceptions {

	public static class BookNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public BookNotFoundException(String googleId) {
			super("Book not found for Google ID: " + googleId);
		}
	}

	public static class InvalidBookDataException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public InvalidBookDataException(String googleId) {
			super("Invalid or missing data returned for Google ID: " + googleId);
		}
	}
}
