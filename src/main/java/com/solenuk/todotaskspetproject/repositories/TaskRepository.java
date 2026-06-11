package com.solenuk.todotaskspetproject.repositories;

import com.solenuk.todotaskspetproject.entities.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    /**
     * Retrieves a list of Tasks created by user.
     *
     * @param creatorId the id of a user to search for; must not be null
     * @return a list of Tasks
     */
    List<Task> findAllByCreatorId(Integer creatorId);

    /**
     * Checks whether a user by given id created any tasks.
     *
     * @param creatorId the id of a user to search for; must not be null
     * @return true if a user has created some tasks, false otherwise
     */
    boolean existsByCreatorId(Integer creatorId);
}
