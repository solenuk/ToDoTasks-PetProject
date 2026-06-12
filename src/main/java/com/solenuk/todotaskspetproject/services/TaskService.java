package com.solenuk.todotaskspetproject.services;

import com.solenuk.todotaskspetproject.dtos.request.CreateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.request.UpdateTaskDTO;
import com.solenuk.todotaskspetproject.dtos.response.ResponseTaskDTO;
import java.util.List;

public interface TaskService {
    ResponseTaskDTO createTask(CreateTaskDTO createTaskRequest);

    ResponseTaskDTO updateTask(Integer id, UpdateTaskDTO updateTaskRequest);

    void deleteTask(Integer id);

    List<ResponseTaskDTO> getAllTasks();

    ResponseTaskDTO getTaskById(Integer id);

    List<ResponseTaskDTO> getTasksByCreatorId(Integer id);
}
