package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.db.Book;

public interface GoogleRepository extends JpaRepository<Book, Long>{

}
