package com.taskmanager.repositories;

import com.taskmanager.models.Role;
import com.taskmanager.models.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRoleOrderByEmailAsc(Role role);
}
