package com.upi.dto.bank;

import com.upi.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {

    private Long id;
    private String bankName;
    private String accountHolderName;
    private String accountNumber; // Masked for security
    private String ifscCode;
    private String accountType;
    private BigDecimal balance;
    private boolean primary;
    private boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}