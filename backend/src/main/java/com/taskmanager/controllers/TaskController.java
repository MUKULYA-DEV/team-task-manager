package com.taskmanager.controllers;

import com.taskmanager.dto.TaskCreateRequest;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.dto.TaskMapper;
import com.taskmanager.models.Project;
import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.User;
import com.taskmanager.repositories.ProjectRepository;
import com.taskmanager.repositories.TaskRepository;
import com.taskmanager.repositories.UserRepository;
import com.taskmanager.security.UserPrincipal;
import com.taskmanager.services.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Member / owner task APIs under {@code /api/tasks}. Admin task creation uses {@link
 * com.taskmanager.controllers.AdminTaskController} at {@code POST /api/admin/tasks}.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService,
                          ProjectRepository projectRepository,
                          UserRepository userRepository,
                          TaskRepository taskRepository) {
        this.taskService = taskService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Tasks assigned to the authenticated user ({@link UserPrincipal#getId()} matches {@code
     * Task.assignee.id}).
     */
    @GetMapping("/my-tasks")
    public List<TaskResponse> myTasks(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return taskService.listMyAssignedTasks(principal.getId());
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(
            Authentication authentication, @RequestParam Long projectId) {
        try {
            return ResponseEntity.ok(
                    taskService.listForProject(authentication.getName(), projectId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createTask(
            Authentication authentication, @Valid @RequestBody TaskRequest taskRequest) {
        
        System.out.println("Received Task Request for ID: " + taskRequest.getAssigneeId());
        
        try {
            Project project = projectRepository.findById(taskRequest.getProjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found"));
            
            User assignee = userRepository.findById(taskRequest.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            Task task = new Task();
            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setProject(project);
            task.setAssignee(assignee);
            task.setStatus(TaskStatus.PENDING);
            
            Task savedTask = taskRepository.save(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(TaskMapper.toResponse(savedTask));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
