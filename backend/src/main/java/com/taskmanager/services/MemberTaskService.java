package com.taskmanager.services;

import com.taskmanager.dto.TaskResponse;
import com.taskmanager.dto.TaskStatusUpdateRequest;
import com.taskmanager.security.UserPrincipal;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberTaskService {

    private final TaskService taskService;

    public MemberTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listMyTasks(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return taskService.listMyAssignedTasks(principal.getId());
    }

    @Transactional
    public TaskResponse updateStatus(
            Authentication authentication, Long taskId, TaskStatusUpdateRequest request) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return taskService.updateMyTaskStatus(principal.getId(), taskId, request);
    }
}
