package com.taskmanager.dto;

import com.taskmanager.models.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskCreateRequest(
        @NotNull Long projectId,
        @NotBlank String title,
        String description,
        TaskStatus status,
        /** When set, task is assigned to this user (must exist and typically {@code MEMBER}). */
        Long assigneeId) {}
