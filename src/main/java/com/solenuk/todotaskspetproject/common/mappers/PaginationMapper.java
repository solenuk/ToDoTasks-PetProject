package com.solenuk.todotaskspetproject.common.mappers;

import com.solenuk.todotaskspetproject.common.dtos.response.PaginatedResponseDTO;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginationMapper {
    /**
     * Converts a Spring Data Page into a generic PaginatedResponse DTO.
     *
     * @param page           The raw Spring Data Page containing entities.
     * @param mapperFunction The specific method reference to convert an Entity (T) to a DTO (R).
     * @param <T>            The Entity type (e.g., Task, User).
     * @param <R>            The DTO type (e.g., ResponseTaskDTO, UserDTO).
     * @return A clean PaginatedResponse containing the mapped DTOs.
     */
    public <T, R> PaginatedResponseDTO<R> mapToPaginatedResponse(Page<T> page, Function<T, R> mapperFunction) {
        List<R> content = page.getContent().stream()
            .map(mapperFunction)
            .toList();

        return new PaginatedResponseDTO<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}
