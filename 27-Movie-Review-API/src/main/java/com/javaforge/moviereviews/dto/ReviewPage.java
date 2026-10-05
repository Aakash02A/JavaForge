package com.javaforge.moviereviews.dto;

import java.util.List;

public record ReviewPage<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
