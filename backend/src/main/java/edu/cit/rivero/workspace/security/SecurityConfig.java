package edu.cit.rivero.workspace.security;

import edu.cit.rivero.workspace.features.auth.*;
import edu.cit.rivero.workspace.common.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    // 1. Added RoleRepository so we can fetch the USER entity
    private final RoleRepository roleRepository;
    private final String frontendOAuthRedirectUri;
    private final EmailService emailService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          AuthenticationProvider authenticationProvider,
                          JwtService jwtService,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          EmailService emailService,
                          @Value("${app.oauth2.frontend-redirect-uri:http://localhost:5173/oauth2/redirect}") String frontendOAuthRedirectUri) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.frontendOAuthRedirectUri = frontendOAuthRedirectUri;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/google").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/spaces/**").permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oAuth2User.getAttribute("email");
                            String name = oAuth2User.getAttribute("name");

                            User user = userRepository.findByEmail(email).orElseGet(() -> {
                                User newUser = new User();
                                newUser.setEmail(email);

                                // 2. Fixed method names to exactly match User.java
                                newUser.setFirstName(name != null ? name.split(" ")[0] : "Google");
                                newUser.setLastName(name != null && name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "User");
                                newUser.setPasswordHash(""); // Fixed to setPasswordHash

                                // 3. Properly fetch the Role entity from the database
                                Role defaultRole = roleRepository.findByRoleName("ROLE_USER").orElseGet(() -> {
                                    Role r = new Role();
                                    r.setRoleName("ROLE_USER");
                                    return roleRepository.save(r);
                                });
                                newUser.setRole(defaultRole);

                                return userRepository.save(newUser);
                            });

                            // Send security alert for OAuth login as well
                            emailService.sendLoginAlertEmail(user.getEmail(), user.getFirstName());

                            String token = jwtService.generateToken(user);
                            response.sendRedirect(frontendOAuthRedirectUri + "?token=" + token);
                        })
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\":false,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"Authentication required\"}}");
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}