package com.taskmanager.services;

import com.taskmanager.dto.AdminTaskCreateRequest;
import com.taskmanager.dto.TaskMapper;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.models.Project;
import com.taskmanager.models.Role;
import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.User;
import com.taskmanager.repositories.ProjectRepository;
import com.taskmanager.repositories.TaskRepository;
import com.taskmanager.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminTaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public AdminTaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponse create(AdminTaskCreateRequest request) {
        Project project =
                projectRepository
                        .findById(request.projectId())
                        .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        User assignee =
                userRepository
                        .findById(request.assigneeId())
                        .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
        if (assignee.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("Tasks can only be assigned to members");
        }
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(TaskStatus.PENDING);
        task.setProject(project);
        task.setAssignee(assignee);
        taskRepository.save(task);
        return TaskMapper.toResponse(task);
    }
}
