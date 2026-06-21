package com.solenuk.todotaskspetproject.mappers;

import com.solenuk.todotaskspetproject.dtos.request.CreateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseTaskDTO;
import com.solenuk.todotaskspetproject.entities.Task;
import com.solenuk.todotaskspetproject.entities.TaskCollaborator;
import com.solenuk.todotaskspetproject.enums.TaskState;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task toEntity(CreateTaskDTO createTaskRequest) {
        return Task.builder()
            .title(createTaskRequest.title())
            .description(createTaskRequest.description())
            .priority(createTaskRequest.priority())
            .state(TaskState.NEW)
            .build();
    }

    public ResponseTaskDTO toResponse(Task task) {
        List<Integer> collaboratorIds = task.getCollaborators() != null
            ? task.getCollaborators().stream()
            .map(TaskCollaborator::getUserId)
            .toList()
            : List.of();

        return new ResponseTaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getState(),
            task.getPriority(),
            task.getCreatorId(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            collaboratorIds
        );
    }
}
