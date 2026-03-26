package com.example.demo.google;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: Calls the real Google Books API.
 *
 * NOTE:
 * This test is disabled because it depends on an external API and may fail due to:
 * - Network issues
 * - API changes
 * - Book data not being available
 */
@SpringBootTest
@Disabled
class GoogleBookServiceSmokeTests {

    @Autowired
    private GoogleBookService googleBookService;

    // ✅ Single test to validate a book instance
    @Test
    void testSearchBooks_ValidateBookFields() {

        GoogleBook result = googleBookService.searchBooks("effective+java", 5, 0);

        assertThat(result).isNotNull();
        assertThat(result.items()).isNotNull();
        assertThat(result.items()).isNotEmpty();

        GoogleBook.Item first = result.items().get(0);

        // ✅ Validate important fields (not exact values)
        assertThat(first.id()).isNotBlank();
        assertThat(first.selfLink()).isNotBlank();
        assertThat(first.volumeInfo()).isNotNull();

        assertThat(first.volumeInfo().title()).isNotBlank();
        assertThat(first.volumeInfo().authors()).isNotNull();
        assertThat(first.volumeInfo().language()).isNotBlank();
    }
}