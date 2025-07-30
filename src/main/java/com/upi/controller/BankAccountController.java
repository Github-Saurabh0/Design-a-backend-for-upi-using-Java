package com.upi.controller;

import com.upi.dto.MessageResponse;
import com.upi.dto.bank.BankAccountRequest;
import com.upi.dto.bank.BankAccountResponse;
import com.upi.model.User;
import com.upi.security.services.UserDetailsImpl;
import com.upi.service.BankAccountService;
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
import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bank-accounts")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Bank Accounts", description = "Bank Account management APIs")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all bank accounts", 
               description = "Retrieves all bank accounts linked to the authenticated user")
    public ResponseEntity<List<BankAccountResponse>> getAllBankAccounts() {
        User currentUser = getCurrentUser();
        List<BankAccountResponse> bankAccounts = bankAccountService.getAllBankAccounts(currentUser);
        return ResponseEntity.ok(bankAccounts);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get bank account by ID", 
               description = "Retrieves a specific bank account by its ID for the authenticated user")
    public ResponseEntity<BankAccountResponse> getBankAccountById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        BankAccountResponse bankAccount = bankAccountService.getBankAccountById(currentUser, id);
        return ResponseEntity.ok(bankAccount);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add a new bank account", 
               description = "Adds a new bank account for the authenticated user")
    public ResponseEntity<BankAccountResponse> addBankAccount(@Valid @RequestBody BankAccountRequest bankAccountRequest) {
        User currentUser = getCurrentUser();
        BankAccountResponse bankAccount = bankAccountService.addBankAccount(currentUser, bankAccountRequest);
        return ResponseEntity.ok(bankAccount);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update a bank account", 
               description = "Updates an existing bank account for the authenticated user")
    public ResponseEntity<BankAccountResponse> updateBankAccount(
            @PathVariable Long id,
            @Valid @RequestBody BankAccountRequest bankAccountRequest) {
        User currentUser = getCurrentUser();
        BankAccountResponse bankAccount = bankAccountService.updateBankAccount(currentUser, id, bankAccountRequest);
        return ResponseEntity.ok(bankAccount);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete a bank account", 
               description = "Deletes a bank account for the authenticated user")
    public ResponseEntity<MessageResponse> deleteBankAccount(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        MessageResponse response = bankAccountService.deleteBankAccount(currentUser, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/primary")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Set a bank account as primary", 
               description = "Sets a specific bank account as the primary account for the authenticated user")
    public ResponseEntity<BankAccountResponse> setPrimaryBankAccount(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        BankAccountResponse bankAccount = bankAccountService.setPrimaryBankAccount(currentUser, id);
        return ResponseEntity.ok(bankAccount);
    }

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    @Operation(summary = "Verify bank account", 
               description = "Verifies a bank account (only accessible to bank admins)")
    public ResponseEntity<MessageResponse> verifyBankAccount(@PathVariable Long id) {
        // In a real application, this would involve bank-specific verification logic
        // For this demo, we'll just mark it as verified
        User currentUser = getCurrentUser();
        MessageResponse response = bankAccountService.verifyBankAccount(currentUser, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get bank account balance", 
               description = "Retrieves the current balance of a specific bank account for the authenticated user")
    public ResponseEntity<BigDecimal> getBankBalance(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        BigDecimal balance = bankAccountService.getBankBalance(currentUser, id);
        return ResponseEntity.ok(balance);
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