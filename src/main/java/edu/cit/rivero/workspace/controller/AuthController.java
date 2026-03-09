package edu.cit.rivero.workspace.controller;

import edu.cit.rivero.workspace.common.ApiResponse;
import edu.cit.rivero.workspace.dto.AuthResponseData;
import edu.cit.rivero.workspace.dto.LoginRequest;
import edu.cit.rivero.workspace.dto.RegisterRequest;
import edu.cit.rivero.workspace.service.AuthService;
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
}