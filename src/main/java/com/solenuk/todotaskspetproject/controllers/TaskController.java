package com.solenuk.todotaskspetproject.controllers;

import com.solenuk.todotaskspetproject.dtos.request.CreateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.response.PaginatedResponseDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseTaskDTO;
import com.solenuk.todotaskspetproject.entities.User;
import com.solenuk.todotaskspetproject.services.TaskService;
import jakarta.validation.Valid;
import java.nio.file.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/all")
    public ResponseEntity<PaginatedResponseDTO<ResponseTaskDTO>> getAllTasks(Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasks(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseTaskDTO> getTaskById(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<ResponseTaskDTO>> getTasksForUser(
        @AuthenticationPrincipal User currentUser, Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksForUser(currentUser.getId(), pageable));
    }

    @PostMapping
    public ResponseEntity<ResponseTaskDTO> createTask(@Valid @RequestBody CreateTaskDTO request,
        @AuthenticationPrincipal User currentUser) {
        ResponseTaskDTO createdTask = taskService.createTask(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseTaskDTO> updateTask(
        @PathVariable Integer id,
        @Valid @RequestBody UpdateTaskDTO request,
        @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        return ResponseEntity.ok(
            taskService.updateTask(id, request, currentUser.getId(), currentUser.getRole().name()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer id, @AuthenticationPrincipal User currentUser)
        throws AccessDeniedException {
        taskService.deleteTask(id, currentUser.getId(), currentUser.getRole().name());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/collaborators/{collaboratorId}")
    public ResponseEntity<Void> addCollaboratorToTask(@PathVariable Integer taskId,
        @PathVariable Integer collaboratorId, @AuthenticationPrincipal User requester) {
        taskService.addCollaborator(taskId, collaboratorId, requester.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{taskId}/collaborators/{collaboratorId}")
    public ResponseEntity<Void> removeCollaborator(@PathVariable Integer taskId,
        @PathVariable Integer collaboratorId, @AuthenticationPrincipal User requester) {
        taskService.removeCollaborator(taskId, collaboratorId, requester.getId());
        return ResponseEntity.noContent().build();
    }
}
