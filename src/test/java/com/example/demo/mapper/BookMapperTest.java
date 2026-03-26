package com.example.demo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.example.demo.db.Book;
import com.example.demo.pojo.BookDTO;
import com.example.demo.pojo.GoogleBookDetail;

class BookMapperTest {

	private BookMapper mapper = Mappers.getMapper(BookMapper.class);

	@Test
	void testEntityToDTO_HappyPath() {
		Book book = new Book("1", "Effective Java", "Joshua Bloch",264);
		//book.setPageCount(264);

		BookDTO dto = mapper.toDTO(book);
		assertEquals("1", dto.getId());
		assertEquals("Effective Java", dto.getTitle());
		assertEquals(264, dto.getPageCount());
	}

	@Test
	void testEntityToDTO_EmptyFields() {
		Book book = new Book();
		BookDTO dto = mapper.toDTO(book);
		assertNull(dto.getTitle());
		assertNull(dto.getAuthor());
	}

	@Test
	void testDTOToEntity_HappyPath() {
		GoogleBookDetail detail = new GoogleBookDetail(null, "BREwDwAAQBAJ", null, new GoogleBookDetail.VolumeInfo(
				"Effective Java", List.of("Joshua Bloch"), null, null, 264, null, null, null, null, null, null));
		Book book = mapper.toEntity(detail);
		assertEquals("BREwDwAAQBAJ", book.getId());
		assertEquals("Effective Java", book.getTitle());
	}
}
