package com.solenuk.todotaskspetproject.task.services.interfaces;

import com.solenuk.todotaskspetproject.common.dtos.response.PaginatedResponseDTO;
import com.solenuk.todotaskspetproject.task.dtos.request.CreateTaskDTO;
import com.solenuk.todotaskspetproject.task.dtos.request.UpdateTaskDTO;
import com.solenuk.todotaskspetproject.task.dtos.response.ResponseTaskDTO;
import java.nio.file.AccessDeniedException;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    ResponseTaskDTO createTask(CreateTaskDTO createTaskRequest, Integer creatorId);

    ResponseTaskDTO updateTask(Integer taskId, UpdateTaskDTO updateTaskRequest, Integer requesterId,
        String userRole) throws AccessDeniedException;

    void deleteTask(Integer taskId, Integer requesterId, String userRole) throws AccessDeniedException;

    PaginatedResponseDTO<ResponseTaskDTO> getAllTasks(Pageable pageable, String userRole)
        throws AccessDeniedException;

    ResponseTaskDTO getTaskById(Integer id);

    PaginatedResponseDTO<ResponseTaskDTO> getTasksForUser(Integer userId, Pageable pageable);

    void addCollaborator(Integer taskId, Integer collaboratorId, Integer requesterId);

    void removeCollaborator(Integer taskId, Integer collaboratorId, Integer requesterId);
}
