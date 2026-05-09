package com.taskmanager.controllers;

import com.taskmanager.dto.AdminProjectCreateRequest;
import com.taskmanager.dto.AdminProjectResponse;
import com.taskmanager.services.AdminProjectService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    private final AdminProjectService adminProjectService;

    public AdminProjectController(AdminProjectService adminProjectService) {
        this.adminProjectService = adminProjectService;
    }

    @GetMapping
    public List<AdminProjectResponse> list() {
        return adminProjectService.listAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AdminProjectCreateRequest request) {
        try {
            AdminProjectResponse created = adminProjectService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
