package com.solenuk.todotaskspetproject.task.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_collaborators")
@Getter
@Setter
@NoArgsConstructor
public class TaskCollaborator {
    @EmbeddedId
    private TaskCollaboratorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    private Task task;

    public TaskCollaborator(Task task, Integer userId) {
        this.task = task;
        this.id = new TaskCollaboratorId(task.getId(), userId);
    }

    public Integer getUserId() {
        return this.id != null ? this.id.getUserId() : null;
    }
}
