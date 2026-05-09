package com.taskmanager.controllers;

import com.taskmanager.dto.ProjectCreateRequest;
import com.taskmanager.dto.ProjectResponse;
import com.taskmanager.services.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> list(Authentication authentication) {
        return projectService.listForOwner(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            Authentication authentication, @Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse created =
                projectService.create(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
