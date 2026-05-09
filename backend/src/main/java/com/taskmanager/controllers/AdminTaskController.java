package com.taskmanager.controllers;

import com.taskmanager.dto.AdminTaskCreateRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.services.AdminTaskService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/tasks")
public class AdminTaskController {

    private final AdminTaskService adminTaskService;

    public AdminTaskController(AdminTaskService adminTaskService) {
        this.adminTaskService = adminTaskService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AdminTaskCreateRequest request) {
        try {
            TaskResponse created = adminTaskService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
