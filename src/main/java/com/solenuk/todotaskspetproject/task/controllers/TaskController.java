package com.solenuk.todotaskspetproject.task.controllers;

import com.solenuk.todotaskspetproject.common.dtos.response.PaginatedResponseDTO;
import com.solenuk.todotaskspetproject.common.enteties.SecurityUser;
import com.solenuk.todotaskspetproject.task.dtos.request.CreateTaskDTO;
import com.solenuk.todotaskspetproject.task.dtos.response.ResponseTaskDTO;
import com.solenuk.todotaskspetproject.task.services.interfaces.TaskService;
import com.solenuk.todotaskspetproject.task.dtos.request.UpdateTaskDTO;
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
    public ResponseEntity<PaginatedResponseDTO<ResponseTaskDTO>> getAllTasks(Pageable pageable,
        @AuthenticationPrincipal SecurityUser currentUser) throws AccessDeniedException {
        return ResponseEntity.ok(taskService.getAllTasks(pageable, extractRole(currentUser)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseTaskDTO> getTaskById(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<ResponseTaskDTO>> getTasksForUser(
        @AuthenticationPrincipal SecurityUser currentUser, Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksForUser(currentUser.id(), pageable));
    }

    @PostMapping
    public ResponseEntity<ResponseTaskDTO> createTask(@Valid @RequestBody CreateTaskDTO request,
        @AuthenticationPrincipal SecurityUser currentUser) {
        ResponseTaskDTO createdTask = taskService.createTask(request, currentUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseTaskDTO> updateTask(
        @PathVariable Integer id,
        @Valid @RequestBody UpdateTaskDTO request,
        @AuthenticationPrincipal SecurityUser currentUser) throws AccessDeniedException {
        return ResponseEntity.ok(
            taskService.updateTask(id, request, currentUser.id(), extractRole(currentUser)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer id, @AuthenticationPrincipal SecurityUser currentUser)
        throws AccessDeniedException {
        taskService.deleteTask(id, currentUser.id(), extractRole(currentUser));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/collaborators/{collaboratorId}")
    public ResponseEntity<Void> addCollaboratorToTask(@PathVariable Integer taskId,
        @PathVariable Integer collaboratorId, @AuthenticationPrincipal SecurityUser requester) {
        taskService.addCollaborator(taskId, collaboratorId, requester.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{taskId}/collaborators/{collaboratorId}")
    public ResponseEntity<Void> removeCollaborator(@PathVariable Integer taskId,
        @PathVariable Integer collaboratorId, @AuthenticationPrincipal SecurityUser requester) {
        taskService.removeCollaborator(taskId, collaboratorId, requester.id());
        return ResponseEntity.noContent().build();
    }

    private String extractRole(SecurityUser user) {
        return user.getAuthorities().iterator().next().getAuthority();
    }
}
