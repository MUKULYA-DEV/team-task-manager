package com.taskmanager.repositories;

import com.taskmanager.models.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOwner_EmailOrderByIdAsc(String ownerEmail);

    Optional<Project> findByIdAndOwner_Email(Long id, String ownerEmail);
}
