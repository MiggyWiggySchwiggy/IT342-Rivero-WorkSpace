package edu.cit.rivero.workspace.service;

import edu.cit.rivero.workspace.dto.AuthResponseData;
import edu.cit.rivero.workspace.dto.LoginRequest;
import edu.cit.rivero.workspace.dto.RegisterRequest;
import edu.cit.rivero.workspace.dto.UserDto;
import edu.cit.rivero.workspace.entity.Role;
import edu.cit.rivero.workspace.entity.User;
import edu.cit.rivero.workspace.repository.RoleRepository;
import edu.cit.rivero.workspace.repository.UserRepository;
import edu.cit.rivero.workspace.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Explicit constructor injection (No Lombok needed)
    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseData register(RegisterRequest request) {
        // 1. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use.");
        }

        // 2. Fetch the default role
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found in database."));

        // 3. Create the User entity and hash the password
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // BCrypt hashing
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setRole(userRole);
        user.setCreatedAt(LocalDateTime.now());

        // 4. Save to database
        userRepository.save(user);

        // 5. Generate tokens and prepare response
        return generateAuthResponse(user);
    }

    public AuthResponseData login(LoginRequest request) {
        // 1. Authenticate user credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Fetch the user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Generate tokens and prepare response
        return generateAuthResponse(user);
    }

    private AuthResponseData generateAuthResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtToken; // For Phase 1, we will reuse the token. Real refresh tokens can be added later!

        UserDto userDto = new UserDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getRoleName()
        );

        return new AuthResponseData(userDto, jwtToken, refreshToken);
    }
}