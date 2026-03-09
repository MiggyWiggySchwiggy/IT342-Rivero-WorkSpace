package edu.cit.rivero.workspace.repository;

import edu.cit.rivero.workspace.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // We will use this to find the default "ROLE_USER" during registration
    Optional<Role> findByRoleName(String roleName);
}