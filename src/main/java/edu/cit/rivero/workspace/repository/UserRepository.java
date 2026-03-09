package edu.cit.rivero.workspace.repository;

import edu.cit.rivero.workspace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Used for login validation
    Optional<User> findByEmail(String email);

    // Used to prevent duplicate emails during registration
    boolean existsByEmail(String email);
}