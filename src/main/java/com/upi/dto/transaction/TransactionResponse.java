package com.upi.dto.transaction;

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
public class TransactionResponse {

    private Long id;
    private String utrNumber;
    private String senderVpa;
    private String receiverVpa;
    private BigDecimal amount;
    private String description;
    private String type;
    private String status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}