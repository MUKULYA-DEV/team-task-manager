package com.taskmanager.dto;

import com.taskmanager.models.Task;

public final class TaskMapper {

    private TaskMapper() {}

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAssignee() != null ? task.getAssignee().getEmail() : null);
    }
}
