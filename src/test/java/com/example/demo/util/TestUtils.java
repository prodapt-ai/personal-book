package com.example.demo.util;

import com.example.demo.google.GoogleBook;
import com.example.demo.pojo.GoogleBookDetail;

import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public class TestUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    // Reusable JSON loader
    public static GoogleBook loadJson(String fileName) throws Exception {
        InputStream is = TestUtils.class
                .getClassLoader()
                .getResourceAsStream(fileName);
        return mapper.readValue(is, GoogleBook.class);
    }

    // Reusable GoogleBookDetail builder
    public static GoogleBookDetail buildGoogleBookDetail(
            String id,
            String title,
            List<String> authors,
            int pageCount
    ) {
        return new GoogleBookDetail(
                id,
                id,
                "selfLink",
                new GoogleBookDetail.VolumeInfo(
                        title,
                        authors,
                        "2020",
                        "publisher",
                        pageCount,
                        "BOOK",
                        "NOT_MATURE",
                        List.of("Programming"),
                        "en",
                        "preview",
                        "info"
                )
        );
    }
}
