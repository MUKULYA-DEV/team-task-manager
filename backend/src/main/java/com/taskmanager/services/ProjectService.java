package com.taskmanager.services;

import com.taskmanager.dto.ProjectCreateRequest;
import com.taskmanager.dto.ProjectResponse;
import com.taskmanager.models.Project;
import com.taskmanager.models.User;
import com.taskmanager.repositories.ProjectRepository;
import com.taskmanager.repositories.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listForOwner(String ownerEmail) {
        return projectRepository.findAllByOwner_EmailOrderByIdAsc(ownerEmail).stream()
                .map(p -> new ProjectResponse(p.getId(), p.getName(), p.getDescription()))
                .toList();
    }

    @Transactional
    public ProjectResponse create(String ownerEmail, ProjectCreateRequest request) {
        User owner =
                userRepository
                        .findByEmail(ownerEmail)
                        .orElseThrow(() -> new IllegalStateException("User not found"));
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setOwner(owner);
        projectRepository.save(project);
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
    }
}
