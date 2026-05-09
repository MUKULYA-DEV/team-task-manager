package com.taskmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminProjectCreateRequest(
        @NotBlank String name, String description, @NotBlank @Email String ownerEmail) {}
