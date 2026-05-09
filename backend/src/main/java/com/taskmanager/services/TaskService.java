package com.taskmanager.services;

import com.taskmanager.dto.TaskCreateRequest;
import com.taskmanager.dto.TaskMapper;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.dto.TaskStatusUpdateRequest;
import com.taskmanager.models.Project;
import com.taskmanager.models.Role;
import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.User;
import com.taskmanager.repositories.ProjectRepository;
import com.taskmanager.repositories.TaskRepository;
import com.taskmanager.repositories.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listForProject(String ownerEmail, Long projectId) {
        Project project =
                projectRepository
                        .findByIdAndOwner_Email(projectId, ownerEmail)
                        .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return taskRepository.findAllByProject_IdOrderByIdAsc(project.getId()).stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    /** Tasks where {@code assignee.id} equals the authenticated user's id. */
    @Transactional(readOnly = true)
    public List<TaskResponse> listMyAssignedTasks(Long authenticatedUserId) {
        return taskRepository.findAllByAssignee_IdOrderByIdAsc(authenticatedUserId).stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Transactional
    public TaskResponse updateMyTaskStatus(
            Long authenticatedUserId, Long taskId, TaskStatusUpdateRequest request) {
        Task task =
                taskRepository
                        .findByIdAndAssignee_Id(taskId, authenticatedUserId)
                        .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setStatus(request.status());
        taskRepository.save(task);
        return TaskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse create(String ownerEmail, TaskCreateRequest request) {
        Project project =
                projectRepository
                        .findByIdAndOwner_Email(request.projectId(), ownerEmail)
                        .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status() != null ? request.status() : TaskStatus.PENDING);
        task.setProject(project);
        if (request.assigneeId() != null) {
            User assignee =
                    userRepository
                            .findById(request.assigneeId())
                            .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            if (assignee.getRole() != Role.MEMBER) {
                throw new IllegalArgumentException("Tasks can only be assigned to members");
            }
            task.setAssignee(assignee);
        }
        taskRepository.save(task);
        return TaskMapper.toResponse(task);
    }
}
