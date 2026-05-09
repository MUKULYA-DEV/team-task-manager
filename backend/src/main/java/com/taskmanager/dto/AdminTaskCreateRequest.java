package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminTaskCreateRequest(
        @NotNull Long projectId,
        @NotBlank String title,
        String description,
        @NotNull Long assigneeId) {}
