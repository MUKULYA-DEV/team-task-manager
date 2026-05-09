package com.taskmanager.dto;

import com.taskmanager.models.TaskStatus;

public record TaskResponse(
        Long id,
        Long projectId,
        String projectName,
        String title,
        String description,
        TaskStatus status,
        String assigneeEmail) {}
