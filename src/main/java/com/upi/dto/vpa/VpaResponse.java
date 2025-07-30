package com.upi.dto.vpa;

import com.upi.dto.bank.BankAccountResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VpaResponse {

    private Long id;
    private String address; // Complete VPA (username@handle)
    private BankAccountResponse bankAccount;
    private boolean primary;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}