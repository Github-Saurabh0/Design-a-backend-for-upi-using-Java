package com.upi.controller;

import com.upi.dto.MessageResponse;
import com.upi.dto.vpa.VpaRequest;
import com.upi.dto.vpa.VpaResponse;
import com.upi.model.User;
import com.upi.security.services.UserDetailsImpl;
import com.upi.service.VpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vpas")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Virtual Payment Addresses", description = "VPA management APIs")
public class VpaController {

    @Autowired
    private VpaService vpaService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all VPAs", 
               description = "Retrieves all VPAs linked to the authenticated user")
    public ResponseEntity<List<VpaResponse>> getAllVpas() {
        User currentUser = getCurrentUser();
        List<VpaResponse> vpas = vpaService.getAllVpas(currentUser);
        return ResponseEntity.ok(vpas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get VPA by ID", 
               description = "Retrieves a specific VPA by its ID for the authenticated user")
    public ResponseEntity<VpaResponse> getVpaById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        VpaResponse vpa = vpaService.getVpaById(currentUser, id);
        return ResponseEntity.ok(vpa);
    }

    @GetMapping("/address/{address}")
    @Operation(summary = "Get VPA by address", 
               description = "Retrieves a VPA by its address (public API, no authentication required)")
    public ResponseEntity<VpaResponse> getVpaByAddress(@PathVariable String address) {
        VpaResponse vpa = vpaService.getVpaByAddress(address);
        return ResponseEntity.ok(vpa);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new VPA", 
               description = "Creates a new VPA for the authenticated user")
    public ResponseEntity<VpaResponse> createVpa(@Valid @RequestBody VpaRequest vpaRequest) {
        User currentUser = getCurrentUser();
        VpaResponse vpa = vpaService.createVpa(currentUser, vpaRequest);
        return ResponseEntity.ok(vpa);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update a VPA", 
               description = "Updates an existing VPA for the authenticated user")
    public ResponseEntity<VpaResponse> updateVpa(
            @PathVariable Long id,
            @Valid @RequestBody VpaRequest vpaRequest) {
        User currentUser = getCurrentUser();
        VpaResponse vpa = vpaService.updateVpa(currentUser, id, vpaRequest);
        return ResponseEntity.ok(vpa);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete a VPA", 
               description = "Deletes a VPA for the authenticated user")
    public ResponseEntity<MessageResponse> deleteVpa(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        MessageResponse response = vpaService.deleteVpa(currentUser, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/primary")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Set a VPA as primary", 
               description = "Sets a specific VPA as the primary VPA for the authenticated user")
    public ResponseEntity<VpaResponse> setPrimaryVpa(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        VpaResponse vpa = vpaService.setPrimaryVpa(currentUser, id);
        return ResponseEntity.ok(vpa);
    }

    @GetMapping("/bank-account/{bankAccountId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get VPAs by bank account", 
               description = "Retrieves all VPAs linked to a specific bank account for the authenticated user")
    public ResponseEntity<List<VpaResponse>> getVpasByBankAccount(@PathVariable Long bankAccountId) {
        User currentUser = getCurrentUser();
        List<VpaResponse> vpas = vpaService.getVpasByBankAccount(currentUser, bankAccountId);
        return ResponseEntity.ok(vpas);
    }

    @GetMapping("/validate/{vpaAddress}")
    @Operation(summary = "Validate a VPA", 
               description = "Validates if a VPA exists and is valid (public API, no authentication required)")
    public ResponseEntity<Boolean> validateVpa(@PathVariable String vpaAddress) {
        boolean isValid = vpaService.validateVpa(vpaAddress);
        return ResponseEntity.ok(isValid);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = new User();
        user.setId(userDetails.getId());
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        
        return user;
    }
}