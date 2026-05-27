package edu.cit.rivero.workspace.features.auth;

import edu.cit.rivero.workspace.common.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*") // Allows your React frontend to communicate with this backend
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseData>> register(@RequestBody RegisterRequest request) {
        AuthResponseData responseData = authService.register(request);
        return new ResponseEntity<>(ApiResponse.success(responseData), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseData>> login(@RequestBody LoginRequest request) {
        AuthResponseData responseData = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(responseData));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponseData>> googleLogin(@RequestBody GoogleLoginRequest request) {
        AuthResponseData responseData = authService.googleLogin(request.getIdToken());
        return ResponseEntity.ok(ApiResponse.success(responseData));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UserDto userDto = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }
}