package com.taskmanager.services;

import com.taskmanager.dto.AdminProjectCreateRequest;
import com.taskmanager.dto.AdminProjectResponse;
import com.taskmanager.models.Project;
import com.taskmanager.models.User;
import com.taskmanager.repositories.ProjectRepository;
import com.taskmanager.repositories.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public AdminProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminProjectResponse> listAll() {
        return projectRepository.findAll().stream()
                .map(
                        p ->
                                new AdminProjectResponse(
                                        p.getId(),
                                        p.getName(),
                                        p.getDescription(),
                                        p.getOwner().getEmail()))
                .toList();
    }

    @Transactional
    public AdminProjectResponse create(AdminProjectCreateRequest request) {
        User owner =
                userRepository
                        .findByEmail(request.ownerEmail())
                        .orElseThrow(() -> new IllegalArgumentException("Owner user not found"));
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setOwner(owner);
        projectRepository.save(project);
        return new AdminProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                owner.getEmail());
    }
}
