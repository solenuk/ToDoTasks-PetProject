package com.solenuk.todotaskspetproject.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.solenuk.todotaskspetproject.task.controllers.TaskController;
import com.solenuk.todotaskspetproject.task.dtos.request.CreateTaskDTO;
import com.solenuk.todotaskspetproject.task.dtos.request.UpdateTaskDTO;
import com.solenuk.todotaskspetproject.task.dtos.response.ResponseTaskDTO;
import com.solenuk.todotaskspetproject.task.enums.TaskPriority;
import com.solenuk.todotaskspetproject.task.enums.TaskState;
import com.solenuk.todotaskspetproject.task.services.interfaces.TaskService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TaskService taskService;

    private final Integer defaultId = 1;
    private final String defaultTitle = "Test Title";
    private final String defaultDescription = "Test Description.";
    private final TaskPriority defaultPriority = TaskPriority.HIGH;
    private final TaskState defaultState = TaskState.NEW;
    private final Integer defaultCreatorId = 1;
    private final Integer defaultCollaboratorId = 2;

    @Test
    @DisplayName("POST /api/tasks should return 201 Created and DTO when valid")
    void createTask_ShouldReturn201_WhenRequestIsValid() throws Exception {
        CreateTaskDTO requestDTO = new CreateTaskDTO(
            defaultTitle, defaultDescription, defaultPriority, defaultCreatorId
        );
        ResponseTaskDTO responseDTO = new ResponseTaskDTO(
            defaultId, defaultTitle, defaultDescription, defaultState, defaultPriority, defaultCreatorId, Instant.now(),
            null
        );

        when(taskService.createTask(any(CreateTaskDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(defaultId))
            .andExpect(jsonPath("$.title").value(defaultTitle))
            .andExpect(jsonPath("$.state").value(defaultState.name()));
    }

    @Test
    @DisplayName("POST /api/tasks should return 400 Bad Request when validation fails")
    void createTask_ShouldReturn400_WhenValidationFails() throws Exception {
        // Invalid request: blank title and null priority
        CreateTaskDTO invalidRequest = new CreateTaskDTO(
            "", defaultDescription, null, defaultCreatorId
        );

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.priority").exists());
    }

    @Test
    @DisplayName("GET /api/tasks should return 200 OK and a list of tasks")
    void getAllTasks_ShouldReturn200() throws Exception {
        ResponseTaskDTO responseDTO = new ResponseTaskDTO(
            defaultId, defaultTitle, defaultDescription, defaultState, defaultPriority, defaultCreatorId, Instant.now(),
            null
        );

        when(taskService.getAllTasks()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(defaultId))
            .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} should return 200 OK when task exists")
    void getTaskById_ShouldReturn200_WhenTaskExists() throws Exception {
        ResponseTaskDTO responseDTO = new ResponseTaskDTO(
            defaultId, defaultTitle, defaultDescription, defaultState, defaultPriority, defaultCreatorId, Instant.now(),
            null
        );

        when(taskService.getTaskById(defaultId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/tasks/{id}", defaultId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(defaultId));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} should return 404 Not Found when task is missing")
    void getTaskById_ShouldReturn404_WhenTaskIsMissing() throws Exception {
        Integer missingId = 99;

        when(taskService.getTaskById(missingId)).thenThrow(new EntityNotFoundException("Task not found"));

        mockMvc.perform(get("/api/tasks/{id}", missingId))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId} should return 200 OK and user's tasks")
    void getTasksForUser_ShouldReturn200() throws Exception {
        ResponseTaskDTO responseDTO = new ResponseTaskDTO(
            defaultId, defaultTitle, defaultDescription, defaultState, defaultPriority, defaultCreatorId, Instant.now(),
            null
        );

        when(taskService.getTasksForUser(defaultCreatorId)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/tasks/user/{userId}", defaultCreatorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].creatorId").value(defaultCreatorId));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} should return 200 OK when successful")
    void updateTask_ShouldReturn200_WhenSuccessful() throws Exception {
        UpdateTaskDTO updateRequest = new UpdateTaskDTO(
            "Updated Title", "Updated Desc", TaskState.DOING, TaskPriority.HIGH
        );
        ResponseTaskDTO responseDTO = new ResponseTaskDTO(
            defaultId, "Updated Title", "Updated Desc", TaskState.DOING, TaskPriority.HIGH, defaultCreatorId,
            Instant.now(), Instant.now()
        );

        when(taskService.updateTask(eq(defaultId), any(UpdateTaskDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/tasks/{id}", defaultId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.state").value(TaskState.DOING.name()));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} should return 204 No Content")
    void deleteTask_ShouldReturn204_WhenSuccessful() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", defaultId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} should return 404 Not Found when task is missing")
    void deleteTask_ShouldReturn404_WhenTaskIsMissing() throws Exception {
        Integer missingId = 99;

        doThrow(new EntityNotFoundException("Task not found")).when(taskService).deleteTask(missingId);

        mockMvc.perform(delete("/api/tasks/{id}", missingId))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/tasks/{taskId}/collaborators/{collaboratorId} should return 400 when unauthorized action occurs")
    void addCollaborator_ShouldReturn400_WhenServiceThrowsIllegalArgument() throws Exception {
        Integer invalidRequesterId = 72;

        doThrow(new IllegalArgumentException("Only the task creator can add collaborators."))
            .when(taskService).addCollaborator(defaultId, defaultCollaboratorId, invalidRequesterId);

        mockMvc.perform(post("/api/tasks/{taskId}/collaborators/{collaboratorId}", defaultId, defaultCollaboratorId)
                .param("requesterId", invalidRequesterId.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{taskId}/collaborators/{collaboratorId} should return 204 No Content")
    void removeCollaborator_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/tasks/{taskId}/collaborators/{collaboratorId}", defaultId, defaultCollaboratorId)
                .param("requesterId", defaultCreatorId.toString()))
            .andExpect(status().isNoContent());

        verify(taskService, times(1)).removeCollaborator(defaultId, defaultCreatorId, defaultCollaboratorId);
    }
}
