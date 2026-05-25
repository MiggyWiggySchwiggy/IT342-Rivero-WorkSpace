package edu.cit.rivero.workspace.features.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.rivero.workspace.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass security filters for unit testing controller
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; // Ignore filter if Spring tries to load it

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ReturnsCreated() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");
        request.setFirstname("John");
        request.setLastname("Doe");

        UserDto userDto = new UserDto(1, "test@test.com", "John", "Doe", "ROLE_USER");
        AuthResponseData responseData = new AuthResponseData(userDto, "jwt_token", "refresh_token");

        when(authService.register(any(RegisterRequest.class))).thenReturn(responseData);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt_token"))
                .andExpect(jsonPath("$.data.user.email").value("test@test.com"));
    }

    @Test
    void login_ReturnsOk() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        UserDto userDto = new UserDto(1, "test@test.com", "John", "Doe", "ROLE_USER");
        AuthResponseData responseData = new AuthResponseData(userDto, "jwt_token", "refresh_token");

        when(authService.login(any(LoginRequest.class))).thenReturn(responseData);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt_token"));
    }

    @Test
    void getCurrentUser_ReturnsOk() throws Exception {
        UserDto userDto = new UserDto(1, "test@test.com", "John", "Doe", "ROLE_USER");

        when(authService.getCurrentUser()).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/auth/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@test.com"));
    }
}
