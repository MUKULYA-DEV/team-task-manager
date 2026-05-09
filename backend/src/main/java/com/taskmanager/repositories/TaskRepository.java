package com.taskmanager.repositories;

import com.taskmanager.models.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProject_IdOrderByIdAsc(Long projectId);

    List<Task> findAllByAssignee_EmailOrderByIdAsc(String assigneeEmail);

    /**
     * Same as {@link #findAllByAssignee_EmailOrderByIdAsc} — explicit name for "find by assignee
     * email".
     */
    @Query("select t from Task t where t.assignee is not null and t.assignee.email = :email order by t.id asc")
    List<Task> findByAssigneeEmail(@Param("email") String email);

    List<Task> findAllByAssignee_IdOrderByIdAsc(Long assigneeId);

    Optional<Task> findByIdAndAssignee_Email(Long id, String assigneeEmail);

    Optional<Task> findByIdAndAssignee_Id(Long id, Long assigneeId);
}
