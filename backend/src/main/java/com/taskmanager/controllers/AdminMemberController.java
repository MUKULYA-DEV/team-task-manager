package com.taskmanager.controllers;

import com.taskmanager.dto.MemberSummaryResponse;
import com.taskmanager.services.AdminMemberService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    public AdminMemberController(AdminMemberService adminMemberService) {
        this.adminMemberService = adminMemberService;
    }

    @GetMapping
    public List<MemberSummaryResponse> list() {
        return adminMemberService.listMembers();
    }
}
