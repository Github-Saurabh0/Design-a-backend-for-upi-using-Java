package com.upi.service.impl;

import com.upi.dto.MessageResponse;
import com.upi.dto.auth.JwtResponse;
import com.upi.dto.auth.LoginRequest;
import com.upi.dto.auth.SignupRequest;
import com.upi.model.Role;
import com.upi.model.User;
import com.upi.repository.RoleRepository;
import com.upi.repository.UserRepository;
import com.upi.security.jwt.JwtUtils;
import com.upi.security.services.UserDetailsImpl;
import com.upi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getPhone(),
                roles);
    }

    @Override
    public MessageResponse registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!", false);
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!", false);
        }

        if (userRepository.existsByPhone(signupRequest.getPhone())) {
            return new MessageResponse("Error: Phone number is already in use!", false);
        }

        // Create new user's account
        User user = User.builder()
                .username(signupRequest.getUsername())
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .email(signupRequest.getEmail())
                .phone(signupRequest.getPhone())
                .password(encoder.encode(signupRequest.getPassword()))
                .build();

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "bank_admin":
                        Role bankAdminRole = roleRepository.findByName(Role.ERole.ROLE_BANK_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(bankAdminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!", true);
    }
}