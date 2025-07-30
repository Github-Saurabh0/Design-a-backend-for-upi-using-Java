package com.upi.controller;

import com.upi.dto.MessageResponse;
import com.upi.dto.auth.JwtResponse;
import com.upi.dto.auth.LoginRequest;
import com.upi.dto.auth.SignupRequest;
import com.upi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API for user signup and login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    @Operation(summary = "Authenticate user and generate JWT token", 
               description = "Authenticates a user with username/email/phone and password, and returns a JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", 
               description = "Registers a new user with the provided details and returns a success message")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        MessageResponse response = authService.registerUser(signupRequest);
        return ResponseEntity.ok(response);
    }
}