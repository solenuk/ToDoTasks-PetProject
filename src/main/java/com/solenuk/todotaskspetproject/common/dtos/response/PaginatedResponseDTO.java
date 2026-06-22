package com.solenuk.todotaskspetproject.common.dtos.response;

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
