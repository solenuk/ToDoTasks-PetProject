package com.solenuk.todotaskspetproject.services.impl;

import com.solenuk.todotaskspetproject.dtos.request.CreateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.response.PaginatedResponseDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseTaskDTO;
import com.solenuk.todotaskspetproject.entities.Task;
import com.solenuk.todotaskspetproject.entities.TaskCollaborator;
import com.solenuk.todotaskspetproject.mappers.PaginationMapper;
import com.solenuk.todotaskspetproject.mappers.TaskMapper;
import com.solenuk.todotaskspetproject.repositories.TaskRepository;
import com.solenuk.todotaskspetproject.services.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final PaginationMapper paginationMapper;

    @Override
    @Transactional
    public ResponseTaskDTO createTask(CreateTaskDTO createTaskRequest) {
        // Once JWT authentication is added, this ID will be securely extracted from the token,
        // guaranteeing the user exists
        Task task = taskMapper.toEntity(createTaskRequest);
        Task createdTask = taskRepository.save(task);
        return taskMapper.toResponse(createdTask);
    }

    @Override
    @Transactional
    public ResponseTaskDTO updateTask(Integer id, UpdateTaskDTO updateTaskRequest) {
        Task existingTask = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        existingTask.setTitle(updateTaskRequest.title());
        existingTask.setDescription(updateTaskRequest.description());
        existingTask.setState(updateTaskRequest.state());
        existingTask.setPriority(updateTaskRequest.priority());

        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Integer id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<ResponseTaskDTO> getAllTasks(Pageable pageable) {
        Page<Task> taskPage = taskRepository.findAll(pageable);
        return paginationMapper.mapToPaginatedResponse(taskPage, taskMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseTaskDTO getTaskById(Integer id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<ResponseTaskDTO> getTasksForUser(Integer userId, Pageable pageable) {
        Page<Task> taskPage = taskRepository.findAllTasksForUser(userId, pageable);
        return paginationMapper.mapToPaginatedResponse(taskPage, taskMapper::toResponse);
    }

    @Override
    @Transactional
    public void addCollaborator(Integer taskId, Integer collaboratorId, Integer requesterId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        if (!task.getCreatorId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the task creator can add collaborators.");
        }

        if (task.getCreatorId().equals(collaboratorId)) {
            throw new IllegalArgumentException("The creator of this task cannot be added as collaborator.");
        }

        boolean alreadyCollaborator = task.getCollaborators().stream()
            .anyMatch(c -> c.getUserId().equals(collaboratorId));

        if (!alreadyCollaborator) {
            TaskCollaborator collaborator = new TaskCollaborator(task, collaboratorId);
            task.getCollaborators().add(collaborator);
            taskRepository.save(task);
        }
    }

    @Override
    public void removeCollaborator(Integer taskId, Integer collaboratorId, Integer requesterId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        if (!task.getCreatorId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the task creator can remove collaborators.");
        }

        boolean removed = task.getCollaborators().removeIf(c -> c.getUserId().equals(collaboratorId));

        if (removed) {
            taskRepository.save(task);
        } else {
            throw new EntityNotFoundException("User is not a collaborator on this task.");
        }
    }
}
