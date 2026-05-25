package edu.cit.rivero.workspace.features.auth;

import edu.cit.rivero.workspace.security.*;
import edu.cit.rivero.workspace.common.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EmailService emailService;

    // Explicit constructor injection (No Lombok needed)
    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService,
                       AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
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

        // Send welcome email (asynchronously or fire-and-forget in real app, here sync is fine for demo)
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

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

        // Send a security alert email upon successful login
        emailService.sendLoginAlertEmail(user.getEmail(), user.getFirstName());

        // 3. Generate tokens and prepare response
        return generateAuthResponse(user);
    }

    private AuthResponseData generateAuthResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtToken; // For Phase 1, we will reuse the token. Real refresh tokens can be added later!

        UserDto userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getRoleName()
        );

        return new AuthResponseData(userDto, jwtToken, refreshToken);
    }

    public UserDto getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));

        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getRoleName()
        );
    }
}