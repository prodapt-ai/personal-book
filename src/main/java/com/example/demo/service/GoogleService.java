package com.example.demo.service;

import com.example.demo.pojo.BookDTO;

public interface GoogleService {

    BookDTO fetchAndSaveBook(String googleId);

}
