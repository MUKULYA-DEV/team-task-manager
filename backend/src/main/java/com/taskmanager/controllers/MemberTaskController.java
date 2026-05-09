package com.taskmanager.controllers;

import com.taskmanager.dto.TaskResponse;
import com.taskmanager.dto.TaskStatusUpdateRequest;
import com.taskmanager.services.MemberTaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member/tasks")
public class MemberTaskController {

    private final MemberTaskService memberTaskService;

    public MemberTaskController(MemberTaskService memberTaskService) {
        this.memberTaskService = memberTaskService;
    }

    @GetMapping
    public List<TaskResponse> list(Authentication authentication) {
        return memberTaskService.listMyTasks(authentication);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request,
            Authentication authentication) {
        try {
            TaskResponse updated = memberTaskService.updateStatus(authentication, id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
