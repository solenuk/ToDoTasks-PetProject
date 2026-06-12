package com.solenuk.todotaskspetproject.dtos.request;

import com.solenuk.todotaskspetproject.enums.TaskPriority;
import com.solenuk.todotaskspetproject.enums.TaskState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskDTO(
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must be less than 255 characters")
    String title,

    String description,

    @NotNull(message = "State is required")
    TaskState state,

    @NotNull(message = "Priority is required")
    TaskPriority priority
) {
}
