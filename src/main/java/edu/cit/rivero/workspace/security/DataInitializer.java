package edu.cit.rivero.workspace.security;

import edu.cit.rivero.workspace.features.auth.Role;
import edu.cit.rivero.workspace.features.auth.RoleRepository;
import edu.cit.rivero.workspace.features.auth.User;
import edu.cit.rivero.workspace.features.auth.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN").orElse(null);
        if (adminRole == null) {
            System.out.println("ROLE_ADMIN not found, waiting for data.sql to populate...");
            return;
        }

        if (userRepository.findByEmail("admin@workspace.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@workspace.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setRole(adminRole);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("Default Admin account created: admin@workspace.com / admin123");
        }
    }
}
