package com.solenuk.todotaskspetproject.task.dtos.response;

import com.solenuk.todotaskspetproject.task.enums.TaskPriority;
import com.solenuk.todotaskspetproject.task.enums.TaskState;
import java.time.Instant;
import java.util.List;

public record ResponseTaskDTO(
    Integer id,
    String title,
    String description,
    TaskState state,
    TaskPriority priority,
    Integer creatorId,
    Instant createdAt,
    Instant updatedAt,
    List<Integer> collaboratorIds
) {
}
