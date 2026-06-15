package com.solenuk.todotaskspetproject.repositories;

import com.solenuk.todotaskspetproject.entities.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Retrieves all tasks that a user has access to, either as the creator or as a collaborator.
     *
     * @param userId the id of the user; must not be null
     * @return a list of Tasks accessible by the user
     */
    @Query(
        "SELECT DISTINCT t FROM Task t LEFT JOIN t.collaborators tc WHERE t.creatorId = :userId OR tc.id.userId = "
            + ":userId")
    List<Task> findAllTasksForUser(@Param("userId") Integer userId);

    /**
     * Checks whether a user has access to a specific task, i.e., the user is either the creator
     * or a collaborator of that task.
     *
     * @param taskId the id of the task to check; must not be null
     * @param userId the id of the user to verify access for; must not be null
     * @return true if the user has access to the task, false otherwise
     */
    @Query(
        "SELECT COUNT(t) > 0 FROM Task t LEFT JOIN t.collaborators tc WHERE t.id = :taskId AND (t.creatorId = :userId"
            + " OR tc.id.userId = :userId)")
    boolean hasAccessToTask(@Param("taskId") Integer taskId, @Param("userId") Integer userId);
}
