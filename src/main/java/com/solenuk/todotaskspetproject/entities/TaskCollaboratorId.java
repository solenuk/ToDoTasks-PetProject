package com.solenuk.todotaskspetproject.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class TaskCollaboratorId implements Serializable {
    @NotNull
    @Column(name = "task_id", nullable = false)
    private Integer taskId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;
}