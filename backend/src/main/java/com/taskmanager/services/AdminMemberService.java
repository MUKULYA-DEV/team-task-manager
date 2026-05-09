package com.taskmanager.services;

import com.taskmanager.dto.MemberSummaryResponse;
import com.taskmanager.models.Role;
import com.taskmanager.repositories.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminMemberService {

    private final UserRepository userRepository;

    public AdminMemberService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberSummaryResponse> listMembers() {
        return userRepository.findAllByRoleOrderByEmailAsc(Role.MEMBER).stream()
                .map(u -> new MemberSummaryResponse(u.getId(), u.getEmail()))
                .toList();
    }
}
