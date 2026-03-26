package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    @JsonProperty("bookId")
    private String id;

    @JsonProperty("bookTitle")
    private String title;

    @JsonProperty("bookAuthor")
    private String author;

    @JsonProperty("pageCount")
    private Integer pageCount;
}