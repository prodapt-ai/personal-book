package com.example.demo.pojo;

import java.util.List;

public record GoogleBookDetail(
        String kind,
        String id,
        String selfLink,
        VolumeInfo volumeInfo
) {

    public record VolumeInfo(
            String title,
            List<String> authors,
            String publishedDate,
            String publisher,
            Integer pageCount,
            String printType,
            String maturityRating,
            List<String> categories,
            String language,
            String previewLink,
            String infoLink
    ) {}
}
