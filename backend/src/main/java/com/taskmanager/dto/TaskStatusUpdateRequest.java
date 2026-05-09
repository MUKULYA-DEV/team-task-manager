package com.taskmanager.dto;

import com.taskmanager.models.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(@NotNull TaskStatus status) {}
