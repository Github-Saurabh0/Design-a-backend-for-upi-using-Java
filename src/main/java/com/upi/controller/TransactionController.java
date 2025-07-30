package com.upi.controller;

import com.upi.dto.transaction.TransactionRequest;
import com.upi.dto.transaction.TransactionResponse;
import com.upi.model.User;
import com.upi.security.services.UserDetailsImpl;
import com.upi.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transactions")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transactions", description = "Transaction management APIs")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Initiate a transaction", 
               description = "Initiates a new transaction from the authenticated user's VPA")
    public ResponseEntity<TransactionResponse> initiateTransaction(
            @Valid @RequestBody TransactionRequest transactionRequest) {
        User currentUser = getCurrentUser();
        TransactionResponse transaction = transactionService.initiateTransaction(currentUser, transactionRequest);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{utr}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get transaction by UTR", 
               description = "Retrieves a transaction by its UTR number")
    public ResponseEntity<TransactionResponse> getTransactionByUtr(@PathVariable String utr) {
        TransactionResponse transaction = transactionService.getTransactionByUtr(utr);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all transactions", 
               description = "Retrieves all transactions (sent or received) for the authenticated user")
    public ResponseEntity<Page<TransactionResponse>> getUserTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        User currentUser = getCurrentUser();
        Page<TransactionResponse> transactions = transactionService.getUserTransactions(currentUser, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/sent")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get sent transactions", 
               description = "Retrieves transactions sent from the authenticated user's VPAs")
    public ResponseEntity<Page<TransactionResponse>> getSentTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        User currentUser = getCurrentUser();
        Page<TransactionResponse> transactions = transactionService.getSentTransactions(currentUser, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get received transactions", 
               description = "Retrieves transactions received by the authenticated user's VPAs")
    public ResponseEntity<Page<TransactionResponse>> getReceivedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        User currentUser = getCurrentUser();
        Page<TransactionResponse> transactions = transactionService.getReceivedTransactions(currentUser, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get recent transactions", 
               description = "Retrieves recent transactions (sent or received) for the authenticated user")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
            @RequestParam(defaultValue = "5") int limit) {
        User currentUser = getCurrentUser();
        List<TransactionResponse> transactions = transactionService.getRecentTransactions(currentUser, limit);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/vpa/{vpaAddress}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get transactions by VPA", 
               description = "Retrieves transactions for a specific VPA of the authenticated user")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByVpa(
            @PathVariable String vpaAddress,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        User currentUser = getCurrentUser();
        Page<TransactionResponse> transactions = transactionService.getTransactionsByVpa(currentUser, vpaAddress, pageable);
        return ResponseEntity.ok(transactions);
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