package com.solenuk.todotaskspetproject.services;

import com.solenuk.todotaskspetproject.dtos.request.CreateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.response.PaginatedResponseDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseTaskDTO;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    ResponseTaskDTO createTask(CreateTaskDTO createTaskRequest, Integer creatorId);

    ResponseTaskDTO updateTask(Integer id, UpdateTaskDTO updateTaskRequest);

    void deleteTask(Integer id);

    PaginatedResponseDTO<ResponseTaskDTO> getAllTasks(Pageable pageable);

    ResponseTaskDTO getTaskById(Integer id);

    PaginatedResponseDTO<ResponseTaskDTO> getTasksForUser(Integer userId, Pageable pageable);

    void addCollaborator(Integer taskId, Integer collaboratorId, Integer requesterId);

    void removeCollaborator(Integer taskId, Integer collaboratorId, Integer requesterId);
}
