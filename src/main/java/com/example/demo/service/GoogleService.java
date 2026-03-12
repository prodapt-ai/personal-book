package com.example.demo.service;

import com.example.demo.db.Book;

public interface GoogleService {

	Book fetchByVolumeIdAndSave(String googleId);

}
