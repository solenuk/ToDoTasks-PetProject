package com.solenuk.todotaskspetproject.dtos.response;

import java.util.List;

public record PaginatedResponseDTO<T>(
    List<T> content,
    int pageNo,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean last
) {
}
