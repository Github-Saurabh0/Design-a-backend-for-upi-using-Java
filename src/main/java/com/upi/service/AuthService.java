package com.upi.service;

import com.upi.dto.MessageResponse;
import com.upi.dto.auth.JwtResponse;
import com.upi.dto.auth.LoginRequest;
import com.upi.dto.auth.SignupRequest;

public interface AuthService {

    JwtResponse authenticateUser(LoginRequest loginRequest);

    MessageResponse registerUser(SignupRequest signupRequest);
}