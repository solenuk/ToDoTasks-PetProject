package com.solenuk.todotaskspetproject.dtos.response;

import com.solenuk.todotaskspetproject.enums.TaskPriority;
import com.solenuk.todotaskspetproject.enums.TaskState;
import java.time.Instant;

public record ResponseTaskDTO(
    Integer id,
    String title,
    String description,
    TaskState state,
    TaskPriority priority,
    Integer creatorId,
    Instant createdAt,
    Instant updatedAt
) {
}
