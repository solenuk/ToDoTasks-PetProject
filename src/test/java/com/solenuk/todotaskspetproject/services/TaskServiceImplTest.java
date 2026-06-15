package com.solenuk.todotaskspetproject.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.solenuk.todotaskspetproject.dtos.request.CreateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseTaskDTO;
import com.solenuk.todotaskspetproject.entities.Task;
import com.solenuk.todotaskspetproject.entities.TaskCollaborator;
import com.solenuk.todotaskspetproject.enums.TaskPriority;
import com.solenuk.todotaskspetproject.enums.TaskState;
import com.solenuk.todotaskspetproject.mappers.TaskMapper;
import com.solenuk.todotaskspetproject.repositories.TaskRepository;
import com.solenuk.todotaskspetproject.services.impl.TaskServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    private TaskRepository repository;

    @Mock
    private TaskMapper mapper;

    @InjectMocks
    private TaskServiceImpl service;

    private final Integer defaultId = 1;
    private final String defaultTitle = "Test Task";
    private final String defaultDescription = "This is a test task.";
    private final TaskPriority defaultPriority = TaskPriority.HIGH;
    private final TaskState defaultState = TaskState.NEW;
    private final Integer defaultCreatorId = 42;
    private final Integer defaultCollaboratorId = 2;

    @Test
    @DisplayName("createTask should return DTO when task is created")
    void createTask_ShouldReturnDTO_WhenTaskIsCreated() {
        CreateTaskDTO createRequest = new CreateTaskDTO(
            defaultTitle, defaultDescription, defaultPriority, defaultCreatorId
        );
        Task mockedEntity = new Task();
        ResponseTaskDTO expectedResponse = new ResponseTaskDTO(
            defaultId, defaultTitle, defaultDescription, defaultState, defaultPriority, defaultCreatorId, Instant.now(),
            null
        );

        when(mapper.toEntity(createRequest)).thenReturn(mockedEntity);
        when(repository.save(mockedEntity)).thenReturn(mockedEntity);
        when(mapper.toResponse(mockedEntity)).thenReturn(expectedResponse);

        ResponseTaskDTO actualResponse = service.createTask(createRequest);

        assertNotNull(actualResponse);
        assertEquals(defaultTitle, actualResponse.title());
        verify(repository, times(1)).save(mockedEntity);
    }

    @Test
    @DisplayName("getAllTasks should return a list of DTOs when tasks exist")
    void getAllTasks_ShouldReturnListOfDTOs_WhenTasksExist() {
        Task task1 = new Task();
        Task task2 = new Task();
        List<Task> tasksList = List.of(task1, task2);

        ResponseTaskDTO response1 = new ResponseTaskDTO(
            1, "Task 1", "Desc 1", TaskState.NEW, TaskPriority.LOW, defaultCreatorId, Instant.now(), null
        );
        ResponseTaskDTO response2 = new ResponseTaskDTO(
            2, "Task 2", "Desc 2", TaskState.DOING, TaskPriority.MEDIUM, defaultCreatorId, Instant.now(), null
        );

        when(repository.findAll()).thenReturn(tasksList);
        when(mapper.toResponse(task1)).thenReturn(response1);
        when(mapper.toResponse(task2)).thenReturn(response2);

        List<ResponseTaskDTO> actualResponse = service.getAllTasks();

        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("getTaskById should return DTO when task exists")
    void getTaskById_ShouldReturnDTO_IfTaskExists() {
        Task mockedEntity = new Task();
        ResponseTaskDTO expectedResponse = new ResponseTaskDTO(
            defaultId, defaultTitle, defaultDescription, defaultState, defaultPriority, defaultCreatorId, Instant.now(),
            null
        );

        when(repository.findById(defaultId)).thenReturn(Optional.of(mockedEntity));
        when(mapper.toResponse(mockedEntity)).thenReturn(expectedResponse);

        ResponseTaskDTO actualResponse = service.getTaskById(defaultId);

        assertNotNull(actualResponse);
        assertEquals(defaultId, actualResponse.id());
    }

    @Test
    @DisplayName("getTaskById should throw EntityNotFoundException when task does not exist")
    void getTaskById_ShouldThrowEntityNotFoundException_IfTaskDoesNotExist() {
        Integer missingId = 99;

        when(repository.findById(missingId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> service.getTaskById(missingId)
        );

        assertEquals("Task not found with id: " + missingId, exception.getMessage());
    }

    @Test
    @DisplayName("getTasksForUser should return list of DTOs for a specific creator")
    void getTasksForUser_ShouldReturnListOfDTOs_ForSpecificCreator() {
        Task mockedEntity = new Task();
        List<Task> userTasks = List.of(mockedEntity);
        ResponseTaskDTO expectedResponse = new ResponseTaskDTO(
            defaultId, defaultTitle, defaultDescription, defaultState, defaultPriority, defaultCreatorId, Instant.now(),
            null
        );

        when(repository.findAllTasksForUser(defaultCreatorId)).thenReturn(userTasks);
        when(mapper.toResponse(mockedEntity)).thenReturn(expectedResponse);

        List<ResponseTaskDTO> actualResponse = service.getTasksForUser(defaultCreatorId);

        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        verify(repository, times(1)).findAllTasksForUser(defaultCreatorId);
    }

    @Test
    @DisplayName("updateTask should return updated DTO when successful")
    void updateTask_ShouldReturnUpdatedDTO_WhenSuccessful() {
        Task existingTask = new Task();
        UpdateTaskDTO updateRequest = new UpdateTaskDTO(
            "Updated Title", "Updated Desc", TaskState.DOING, TaskPriority.HIGH
        );
        ResponseTaskDTO expectedResponse = new ResponseTaskDTO(
            defaultId, "Updated Title", "Updated Desc", TaskState.DOING, TaskPriority.HIGH, defaultCreatorId,
            Instant.now(), Instant.now()
        );

        when(repository.findById(defaultId)).thenReturn(Optional.of(existingTask));
        when(repository.save(existingTask)).thenReturn(existingTask);
        when(mapper.toResponse(existingTask)).thenReturn(expectedResponse);

        ResponseTaskDTO actualResponse = service.updateTask(defaultId, updateRequest);

        assertNotNull(actualResponse);
        assertEquals("Updated Title", actualResponse.title());
        assertEquals(TaskState.DOING, actualResponse.state());
        verify(repository, times(1)).save(existingTask);
    }

    @Test
    @DisplayName("deleteTask should call deleteById when task exists")
    void deleteTask_ShouldCallDeleteById_WhenTaskExists() {
        when(repository.existsById(defaultId)).thenReturn(true);

        service.deleteTask(defaultId);

        verify(repository, times(1)).deleteById(defaultId);
    }

    @Test
    @DisplayName("deleteTask should throw EntityNotFoundException when task does not exist")
    void deleteTask_ShouldThrowEntityNotFoundException_IfTaskDoesNotExist() {
        Integer missingId = 99;

        when(repository.existsById(missingId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> service.deleteTask(missingId)
        );

        assertEquals("Task not found with ID: " + missingId, exception.getMessage());
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("addCollaborator should append collaborator when requester is creator and entry is new")
    void addCollaborator_ShouldAddCollaborator_WhenValid() {
        Task task = Task.builder()
            .id(defaultId)
            .title(defaultTitle)
            .creatorId(defaultCreatorId)
            .build();

        when(repository.findById(defaultId)).thenReturn(Optional.of(task));

        service.addCollaborator(defaultId, defaultCollaboratorId, defaultCreatorId);

        assertEquals(1, task.getCollaborators().size());
        assertEquals(defaultCollaboratorId, task.getCollaborators().getFirst().getUserId());
        verify(repository, times(1)).save(task);
    }

    @Test
    @DisplayName("addCollaborator should throw IllegalArgumentException when requester is not the task creator")
    void addCollaborator_ShouldThrowException_WhenRequesterIsNotCreator() {
        Task task = Task.builder()
            .id(defaultId)
            .creatorId(defaultCreatorId)
            .build();
        Integer invalidRequesterId = 500;

        when(repository.findById(defaultId)).thenReturn(Optional.of(task));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.addCollaborator(defaultId, invalidRequesterId, defaultCollaboratorId)
        );

        assertEquals("Only the task creator can add collaborators.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("addCollaborator should throw IllegalArgumentException when trying to add the creator as a collaborator")
    void addCollaborator_ShouldThrowException_WhenCollaboratorIsCreator() {
        Task task = Task.builder()
            .id(defaultId)
            .creatorId(defaultCreatorId)
            .build();

        when(repository.findById(defaultId)).thenReturn(Optional.of(task));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.addCollaborator(defaultId, defaultCreatorId, defaultCreatorId)
        );

        assertEquals("The creator of this task cannot be added as collaborator.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("removeCollaborator should detach collaborator entry when requester is creator")
    void removeCollaborator_ShouldRemoveCollaborator_WhenValid() {
        Task task = Task.builder()
            .id(defaultId)
            .creatorId(defaultCreatorId)
            .build();

        TaskCollaborator existingCollaborator = new TaskCollaborator(task, defaultCollaboratorId);
        task.getCollaborators().add(existingCollaborator);

        when(repository.findById(defaultId)).thenReturn(Optional.of(task));

        service.removeCollaborator(defaultId,defaultCollaboratorId, defaultCreatorId);

        assertTrue(task.getCollaborators().isEmpty());
        verify(repository, times(1)).save(task);
    }

    @Test
    @DisplayName("removeCollaborator should throw EntityNotFoundException when user is not an active collaborator")
    void removeCollaborator_ShouldThrowException_WhenUserIsNotACollaborator() {
        Task task = Task.builder()
            .id(defaultId)
            .creatorId(defaultCreatorId)
            .build();

        when(repository.findById(defaultId)).thenReturn(Optional.of(task));

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> service.removeCollaborator(defaultId, defaultCollaboratorId, defaultCreatorId)
        );

        assertEquals("User is not a collaborator on this task.", exception.getMessage());
        verify(repository, never()).save(any());
    }
}
