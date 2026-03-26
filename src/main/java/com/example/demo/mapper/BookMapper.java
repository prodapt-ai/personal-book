package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.db.Book;
import com.example.demo.pojo.BookDTO;
import com.example.demo.pojo.GoogleBookDetail;

@Mapper(componentModel = "spring")
public interface BookMapper {

	@Mapping(target = "id", source = "id")
	@Mapping(target = "title", source = "volumeInfo.title")
	@Mapping(target = "author", expression = "java(String.join(\",\", googleBook.volumeInfo().authors()))")
	@Mapping(target = "pageCount", source = "volumeInfo.pageCount")
	Book toEntity(GoogleBookDetail googleBook);

	BookDTO toDTO(Book book);
}
